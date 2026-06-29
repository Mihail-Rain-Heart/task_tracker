package com.task.tracker.core.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.task.tracker.core.common.util.Logger
import com.task.tracker.core.data.repository.tasks.TasksRepository
import javax.inject.Inject

class SyncWorkerFactory @Inject constructor(
    private val repository: TasksRepository,
    private val logger: Logger
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return SyncWorker(
            appContext = appContext,
            workerParams = workerParameters,
            repository = repository,
            logger = logger,
        )
    }
}
