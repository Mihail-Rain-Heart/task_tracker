package com.task.tracker.core.data.mapper

import com.task.tracker.core.model.Task
import com.task.tracker.core.database.Task as TaskEntity

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    status = status,
    isSyncRequired = isSyncRequired,
)
