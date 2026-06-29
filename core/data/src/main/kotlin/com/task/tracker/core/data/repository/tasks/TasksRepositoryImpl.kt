package com.task.tracker.core.data.repository.tasks

import com.task.tracker.core.common.extension.runCatchingCancellable
import com.task.tracker.core.common.network.Dispatcher
import com.task.tracker.core.common.network.Dispatchers.IO
import com.task.tracker.core.common.network.NetworkMonitor
import com.task.tracker.core.data.mapper.toDomain
import com.task.tracker.core.data.mapper.toEntity
import com.task.tracker.core.data.mapper.toNetwork
import com.task.tracker.core.database.data.sources.tasks.TasksLocalDataSource
import com.task.tracker.core.model.Task
import com.task.tracker.core.model.TaskStatus
import com.task.tracker.core.network.model.ApiResult
import com.task.tracker.core.network.model.NetworkTask
import com.task.tracker.core.network.sources.tasks.TasksNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.time.Instant
import com.task.tracker.core.database.Task as TaskEntity

internal class TasksRepositoryImpl @Inject constructor(
    @param:Dispatcher(dispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor,
    private val localTasks: TasksLocalDataSource,
    private val networkTasks: TasksNetworkDataSource,
) : TasksRepository {

    private val taskMutexes = ConcurrentHashMap<Long, Mutex>()

    override val tasksFlow: Flow<List<Task>> = localTasks
        .getTasks(context = ioDispatcher)
        .map { tasks ->
            tasks.map { task ->
                task.toDomain()
            }
        }

    override suspend fun getTasks(): Result<Unit> {
        return withContext(context = ioDispatcher) {
            runCatchingCancellable {
                when (val result = networkTasks.getTasks()) {
                    is ApiResult.Success -> saveTasks(tasks = result.data)
                    is ApiResult.NetworkError -> throw result.throwable
                }
            }
        }
    }

    override suspend fun updateStatus(
        id: Long,
        status: TaskStatus,
        updatedAt: Instant
    ): Result<Unit> {
        return withContext(context = ioDispatcher) {
            performStatusUpdate(
                id = id,
                updatedAt = updatedAt,
                status = status,
                restriction = { task -> task?.status == status },
                updateTask = { task ->
                    task.copy(
                        status = status,
                        isSyncRequired = true,
                    )
                }
            )
        }
    }

    override suspend fun syncTask(id: Long, updatedAt: Instant): Result<Unit> {
        return withContext(context = ioDispatcher) {
            performStatusUpdate(
                id = id,
                updatedAt = updatedAt,
                status = null,
                restriction = { task -> task?.isSyncRequired != true },
                updateTask = { task -> task }
            )
        }
    }

    override suspend fun syncTasks(): Result<Unit> {
        return runCatchingCancellable {
            localTasks.getSyncRequiredTasks().forEach { task ->
                syncTask(id = task.id, updatedAt = task.updatedAt)
                    .onFailure { throwable ->
                        throw throwable
                    }
            }
        }
    }

    private suspend fun performStatusUpdate(
        id: Long,
        updatedAt: Instant,
        status: TaskStatus? = null,
        restriction: (localTask: TaskEntity?) -> Boolean,
        updateTask: (localTask: TaskEntity) -> TaskEntity
    ): Result<Unit> {
        return mutex(taskId = id).withLock {
            var localTask: TaskEntity? = null
            var newStatus = status

            runCatchingCancellable {
                localTask = localTasks.getTaskById(id = id)

                if (newStatus == null) {
                    newStatus = localTask?.status
                }

                if (
                    localTask == null ||
                    updatedAt.isOutdated(updatedAt = localTask?.updatedAt) ||
                    restriction(localTask)
                ) {
                    // В БД уже лежит более новая запись, чем эта. Такое могло произойти, например, если refresh успел выполнится раньше.
                    // В идеале, updatedAt должен быть не меньше
                    // Или же операция логически не имеет смысла. Синкуется то, что уже засинкано или устанавливается уже установленный статус.
                    return@runCatchingCancellable
                }

                localTask = updateTask(requireNotNull(value = localTask))

                // Просто убрать нулабельность, return тут невозможен
                val localTask = localTask ?: return@runCatchingCancellable

                if (!networkMonitor.isOnline.value || !networkMonitor.isServerAvailable.value) {
                    // Если нет интернета или сервер моковый не поднят, то нет смысла слать запрос
                    // Всё лежит на совести синка
                    localTasks.setTask(task = localTask)
                    return@runCatchingCancellable
                }

                localTasks.setTask(task = localTask.copy(isSyncing = true))
                val response = networkTasks.updateStatus(id = id, task = localTask.toNetwork())
                when (response) {
                    is ApiResult.Success -> localTasks.setTask(task = response.data.toEntity())
                    is ApiResult.NetworkError -> throw response.throwable
                }
            }.onFailure {
                localTask?.also {
                    localTasks.setTask(
                        task = it.copy(
                            status = newStatus ?: it.status,
                            isSyncRequired = true,
                            isSyncing = false
                        )
                    )
                }
            }
        }
    }

    private suspend fun saveTasks(tasks: List<NetworkTask>) {
        // Работаю с каждым элементом отдельно, потому что синк, получение элементов, рефреш могут выполняться параллельно
        // Из-за этого всегда есть риск, что имея mutex для каждого элемента вместо общего, коллекция полученная списком будет содержать устаревшие элементы.
        // Выбран mutex на каждый элемент, чтобы на больших коллекциях общий mutex не заставлял ждать пользователя когда он руками меняет стейт.
        tasks.forEach { task ->
            currentCoroutineContext().ensureActive() // запись в бд конечно быстрая, но все-таки цикл, ещё и lock, туда-сюда
            mutex(taskId = task.id).withLock {
                val localTask = localTasks.getTaskById(id = task.id)
                if (localTask == null || localTask.isOutdated(task = task)) {
                    localTasks.setTask(task = task.toEntity())
                }
            }
        }
    }

    private fun TaskEntity.isOutdated(task: NetworkTask): Boolean = updatedAt < task.updatedAt

    private fun Instant.isOutdated(updatedAt: Instant?): Boolean {
        updatedAt ?: return false
        return this < updatedAt
    }

    private fun mutex(taskId: Long): Mutex = taskMutexes.getOrPut(taskId) { Mutex() }
}
