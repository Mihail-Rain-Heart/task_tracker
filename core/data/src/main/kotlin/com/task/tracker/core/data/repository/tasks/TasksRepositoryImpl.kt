package com.task.tracker.core.data.repository.tasks

import com.task.tracker.core.database.data.sources.tasks.TasksLocalDataSource
import javax.inject.Inject

internal class TasksRepositoryImpl @Inject constructor(
    private val localTasks: TasksLocalDataSource,
) : TasksRepository
