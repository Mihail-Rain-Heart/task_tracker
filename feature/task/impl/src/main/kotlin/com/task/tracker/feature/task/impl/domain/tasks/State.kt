package com.task.tracker.feature.task.impl.domain.tasks

import com.task.tracker.core.model.Task

internal data class State(
    val tasks: List<Task>,
)