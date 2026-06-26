package com.task.tracker.core.data.mapper

import com.task.tracker.core.model.Task
import com.task.tracker.core.model.TaskStatus
import com.task.tracker.core.network.model.NetworkTask
import com.task.tracker.core.network.model.NetworkTaskStatus
import com.task.tracker.core.database.Task as TaskEntity

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    status = status,
    isSyncRequired = isSyncRequired,
)

fun NetworkTask.toEntity(): TaskEntity = TaskEntity(
    id = id,
    status = status.toDomain(),
    isSyncRequired = false,
    syncVersion = 0,
    updatedAt = updatedAt,
)

fun NetworkTaskStatus.toDomain(): TaskStatus = when (this) {
    NetworkTaskStatus.TODO -> TaskStatus.TODO
    NetworkTaskStatus.IN_PROGRESS -> TaskStatus.IN_PROGRESS
    NetworkTaskStatus.DONE -> TaskStatus.DONE
}

fun TaskStatus.toNetwork(): NetworkTaskStatus = when (this) {
    TaskStatus.TODO -> NetworkTaskStatus.TODO
    TaskStatus.IN_PROGRESS -> NetworkTaskStatus.IN_PROGRESS
    TaskStatus.DONE -> NetworkTaskStatus.DONE
}
