package com.task.tracker.core.data.repository.tasks

import com.task.tracker.core.model.Task
import com.task.tracker.core.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TasksRepository {

    val isOnline: StateFlow<Boolean>

    val tasksFlow: Flow<List<Task>>

    suspend fun getTasks(): Result<Unit>

    suspend fun updateStatus(id: Long, status: TaskStatus): Result<Unit>

    suspend fun syncTask(id: Long): Result<Unit>
}
