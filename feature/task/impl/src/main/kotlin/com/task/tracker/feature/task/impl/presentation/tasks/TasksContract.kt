package com.task.tracker.feature.task.impl.presentation.tasks

import com.task.tracker.core.common.presentation.mvi.Action
import com.task.tracker.core.common.presentation.mvi.Effect
import com.task.tracker.core.common.presentation.mvi.State
import com.task.tracker.feature.task.impl.presentation.tasks.model.UiTask
import com.task.tracker.feature.task.impl.presentation.tasks.model.UiTaskStatus
import kotlin.time.Instant

internal sealed interface TaskAction : Action {

    data object RefreshTasks : TaskAction

    data class RefreshingTasks(val isRefreshing: Boolean) : TaskAction

    data class TasksUpdated(val tasks: List<UiTask>) : TaskAction

    data class OnTaskClicked(
        val id: Long,
        val status: UiTaskStatus,
        val updatedAt: Instant,
    ) : TaskAction

    data class OnTaskSyncClicked(
        val id: Long,
        val updatedAt: Instant,
    ): TaskAction
}

internal sealed interface TaskEffect : Effect {

    data class ShowToast(val message: String) : TaskEffect
}

internal data class TaskState(
    val tasks: List<UiTask>,
    val statuses: List<UiTaskStatus> = UiTaskStatus.entries,
    val isRefreshing: Boolean,
) : State
