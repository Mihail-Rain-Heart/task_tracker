package com.task.tracker.feature.task.impl.presentation.tasks

import androidx.compose.ui.util.fastMap
import androidx.lifecycle.viewModelScope
import com.task.tracker.core.common.presentation.BaseViewModel
import com.task.tracker.core.common.util.Logger
import com.task.tracker.core.domain.ObserveTasksUseCase
import com.task.tracker.core.domain.RefreshTasksUseCase
import com.task.tracker.core.domain.SyncTaskUseCase
import com.task.tracker.core.domain.UpdateTaskStatusUseCase
import com.task.tracker.feature.task.impl.presentation.tasks.model.toDomain
import com.task.tracker.feature.task.impl.presentation.tasks.model.toUiTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.task.tracker.feature.task.impl.presentation.tasks.TaskAction as Action
import com.task.tracker.feature.task.impl.presentation.tasks.TaskEffect as Effect
import com.task.tracker.feature.task.impl.presentation.tasks.TaskState as State

@HiltViewModel
internal class TaskViewModel @Inject constructor(
    logger: Logger,
    observeTaskUseCase: ObserveTasksUseCase,
    private val refreshTaskUseCase: RefreshTasksUseCase,
    private val updateTaskUseCase: UpdateTaskStatusUseCase,
    private val syncTaskUseCase: SyncTaskUseCase,
) : BaseViewModel<Action, State, Effect>(logger = logger) {

    override val mutableState = MutableStateFlow(
        value = State(
            tasks = emptyList(),
            isRefreshing = false,
        )
    )

    private val statusUpdateHashMap = hashMapOf<Long, Job?>()

    init {
        observeTaskUseCase()
            .onEach { tasks ->
                handle(action = Action.TasksUpdated(tasks = tasks.toUiTasks()))
            }
            .launchIn(scope = viewModelScope)

        refreshTasks() // initial load
    }

    override fun handle(action: Action) {
        when (action) {
            is Action.RefreshingTasks -> mutableState.update { state -> state.copy(isRefreshing = action.isRefreshing) }
            is Action.RefreshTasks -> refreshTasks()
            is Action.TasksUpdated -> mutableState.update { state -> state.copy(tasks = action.tasks) }
            is Action.OnTaskClicked -> {
                mutableState.update {
                    it.copy(tasks = it.tasks.fastMap { task ->
                        if (task.id == action.id && action.updatedAt == task.updatedAt) {
                            task.copy(status = action.status)
                        } else {
                            task
                        }
                    })
                }
                statusUpdateHashMap[action.id]?.cancel()
                statusUpdateHashMap[action.id] = viewModelScope.launch {
                    updateTaskUseCase(
                        id = action.id,
                        status = action.status.toDomain(),
                        updatedAt = action.updatedAt,
                    )
                }
            }

            is Action.OnTaskSyncClicked -> {
                statusUpdateHashMap[action.id]?.cancel()
                statusUpdateHashMap[action.id] = viewModelScope.launch {
                    syncTaskUseCase(id = action.id, updatedAt = action.updatedAt)
                }
            }
        }
    }

    private fun refreshTasks() {
        handle(action = Action.RefreshingTasks(isRefreshing = true))
        viewModelScope.launch {
            if (refreshTaskUseCase().isFailure) {
                mutableEffects.emit(value = Effect.ShowToast(message = DEFAULT_ERROR_TOAST_MESSAGE))
            }
            handle(action = Action.RefreshingTasks(isRefreshing = false))
        }
    }
}

private const val DEFAULT_ERROR_TOAST_MESSAGE = "Error"
