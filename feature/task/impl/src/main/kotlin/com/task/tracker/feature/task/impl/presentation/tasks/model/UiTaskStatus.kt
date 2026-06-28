package com.task.tracker.feature.task.impl.presentation.tasks.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.task.tracker.core.model.TaskStatus
import com.task.tracker.feature.task.impl.R

internal enum class UiTaskStatus(
    @param:StringRes
    val label: Int,
    val color: Color,
    val labelColor: Color,
) {

    TODO(
        label = R.string.status_todo,
        color = Color(0xCCFBC02D),
        labelColor = Color(0xFF1A1A1A)
    ),

    IN_PROGRESS(
        label = R.string.status_in_progress,
        color = Color(0xCC1E88E5),
        labelColor = Color(0xFFFFFFFF)
    ),

    DONE(
        label = R.string.status_done,
        color = Color(0xCC43A047),
        labelColor = Color(0xFFFFFFFF)
    )
}

internal fun UiTaskStatus.toDomain(): TaskStatus = when(this) {
    UiTaskStatus.TODO -> TaskStatus.TODO
    UiTaskStatus.IN_PROGRESS -> TaskStatus.IN_PROGRESS
    UiTaskStatus.DONE -> TaskStatus.DONE
}

internal fun TaskStatus.toUiModel(): UiTaskStatus = when(this) {
    TaskStatus.TODO -> UiTaskStatus.TODO
    TaskStatus.IN_PROGRESS -> UiTaskStatus.IN_PROGRESS
    TaskStatus.DONE -> UiTaskStatus.DONE
}
