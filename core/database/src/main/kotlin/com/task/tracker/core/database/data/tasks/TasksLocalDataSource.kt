package com.task.tracker.core.database.data.tasks

import com.task.tracker.core.database.Task

interface TasksLocalDataSource {

    suspend fun getTasks(): List<Task>
}
