package com.task.tracker.core.database.data.tasks

import app.cash.sqldelight.async.coroutines.awaitAsList
import com.task.tracker.core.database.Database
import com.task.tracker.core.database.Task
import javax.inject.Inject

internal class TasksLocalDataSourceImpl @Inject constructor(private val db: Database) : TasksLocalDataSource {

    override suspend fun getTasks(): List<Task> {
        return db.taskQueries.selectAll().awaitAsList()
    }
}
