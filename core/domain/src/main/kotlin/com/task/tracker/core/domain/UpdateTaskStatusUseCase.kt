package com.task.tracker.core.domain

import com.task.tracker.core.data.repository.tasks.TasksRepository
import javax.inject.Inject

class UpdateTaskStatusUseCase @Inject constructor(
    private val repository: TasksRepository,
)
