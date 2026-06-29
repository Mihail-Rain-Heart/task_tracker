package com.task.tracker.core.data.repository.tasks

import com.task.tracker.core.model.Task
import com.task.tracker.core.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface TasksRepository {

    val tasksFlow: Flow<List<Task>>

    suspend fun getTasks(): Result<Unit>

    suspend fun updateStatus(id: Long, status: TaskStatus, updatedAt: Instant): Result<Unit>

    suspend fun syncTask(id: Long, updatedAt: Instant): Result<Unit>

    suspend fun syncTasks(): Result<Unit>
}
