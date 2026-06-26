package com.task.tracker.core.domain

import com.task.tracker.core.data.repository.tasks.TasksRepository
import com.task.tracker.core.model.Task
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase @Inject constructor(
    private val repository: TasksRepository,
) {

    operator fun invoke(): Flow<List<Task>> = repository.tasksFlow
}
