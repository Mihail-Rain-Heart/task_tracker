package com.task.tracker.core.database.data.sources.tasks

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.QueryResult
import com.task.tracker.core.database.Database
import com.task.tracker.core.database.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class TasksLocalDataSourceImpl @Inject constructor(
    private val db: Database,
) : TasksLocalDataSource {

    override fun getTasks(context: CoroutineContext): Flow<List<Task>> {
        return db.taskQueries.selectAll().asFlow().mapToList(context)
    }

    override suspend fun getSyncRequiredTasks(): List<Task> {
        return db.taskQueries.selectSyncRequired().awaitAsList()
    }

    override suspend fun getTaskById(id: Long): Task? {
        return db.taskQueries.selectById(id = id).awaitAsOneOrNull()
    }

    override fun setTask(task: Task) {
        insertTask(task = task)
    }

    override fun setTasks(tasks: List<Task>) {
        db.transaction {
            tasks.forEach(action = ::insertTask)
        }
    }

    private fun insertTask(task: Task): QueryResult<Long> {
        return db.taskQueries.upsert(
            id = task.id,
            status = task.status,
            title = task.title,
            isSyncRequired = task.isSyncRequired,
            isSyncing = task.isSyncing,
            updatedAt = task.updatedAt,
        )
    }
}
