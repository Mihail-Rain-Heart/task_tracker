package com.task.tracker.feature.task.impl.presentation.tasks

internal data class UiState(
    val tasks: List<UiTask>,
    val isRefreshing: Boolean,
)

// todo: добавить недостающее
internal data class UiTask(
    val id: Long,
)
