package com.task.tracker.core.model

data class Task(
    val id: Long,
    val status: TaskStatus,
    val isSyncRequired: Boolean,
)
