package com.task.tracker.core.data.repository.tasks

import com.task.tracker.core.common.di.ApplicationScope
import com.task.tracker.core.common.network.Dispatcher
import com.task.tracker.core.common.network.Dispatchers.IO
import com.task.tracker.core.data.mapper.toDomain
import com.task.tracker.core.data.util.NetworkMonitor
import com.task.tracker.core.database.data.sources.tasks.TasksLocalDataSource
import com.task.tracker.core.model.Task
import com.task.tracker.core.model.TaskStatus
import com.task.tracker.core.network.model.ApiResult
import com.task.tracker.core.network.sources.tasks.TasksNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

internal class TasksRepositoryImpl @Inject constructor(
    @ApplicationScope scope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    @param:Dispatcher(dispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
    private val localTasks: TasksLocalDataSource,
    private val networkTasks: TasksNetworkDataSource,
) : TasksRepository {

    private val taskMutexes = ConcurrentHashMap<Long, Mutex>()

    override val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    override val tasksFlow: Flow<List<Task>> = localTasks
        .getTasks(context = ioDispatcher)
        .map { tasks ->
            tasks.map { task ->
                task.toDomain()
            }
        }

    override suspend fun getTasks(): Result<Unit> {
        return withContext(context = ioDispatcher) {
            when (val result = networkTasks.getTasks()) {
                is ApiResult.Success -> Result.success(value = Unit)
                is ApiResult.NetworkError -> Result.failure(exception = result.throwable)
            }
        }
    }

    override suspend fun updateStatus(
        id: Long,
        status: TaskStatus
    ): Result<Unit> {
        TODO("implement update status")
    }

    override suspend fun syncTask(id: Long): Result<Unit> {
        TODO(" implement sync")
    }

    private fun mutex(taskId: Long): Mutex {
        return taskMutexes.getOrPut(taskId) {
            Mutex()
        }
    }
}
