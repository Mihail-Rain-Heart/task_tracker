package com.task.tracker.feature.task.impl.presentation.tasks.model

import com.task.tracker.core.model.Task
import com.task.tracker.core.ui.utils.UiText
import com.task.tracker.feature.task.impl.R
import kotlin.time.Instant

internal data class UiTask(
    val id: Long,
    val title: UiText,
    val status: UiTaskStatus,
    val isSyncing: Boolean,
    val isSyncRequired: Boolean,
    val updatedAt: Instant,
)

internal fun Task.toUiTask(): UiTask = UiTask(
    id = id,
    title = toUiTitle(),
    status = status.toUiModel(),
    isSyncing = isSyncing,
    isSyncRequired = isSyncRequired,
    updatedAt = updatedAt,
)

internal fun List<Task>.toUiTasks(): List<UiTask> = map { task -> task.toUiTask() }

private fun Task.toUiTitle(): UiText = title?.let {
    UiText.Value(value = it)
} ?: UiText.Resource(resId = R.string.empty_title)
