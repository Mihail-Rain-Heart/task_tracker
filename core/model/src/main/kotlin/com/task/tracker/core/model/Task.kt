package com.task.tracker.core.model

import kotlin.time.Instant

data class Task(
    val id: Long,
    val title: String?,
    val status: TaskStatus,
    val isSyncing: Boolean,
    val isSyncRequired: Boolean,
    val updatedAt: Instant,
)
