package com.task.tracker.core.database.data.sources.tasks

import com.task.tracker.core.database.Task
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

interface TasksLocalDataSource {

    suspend fun getTasks(context: CoroutineContext): Flow<List<Task>>

    suspend fun getTaskById(id: Long): Task?

    fun setTask(task: Task)

    fun setTasks(tasks: List<Task>)
}
