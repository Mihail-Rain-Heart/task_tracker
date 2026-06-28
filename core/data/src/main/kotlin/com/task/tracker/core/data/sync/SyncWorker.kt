package com.task.tracker.core.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.task.tracker.core.common.util.Logger
import com.task.tracker.core.data.repository.tasks.TasksRepository
import com.task.tracker.core.network.utils.BackendException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val repository: TasksRepository,
    @Assisted private val logger: Logger,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        logger.i(TAG, "doWork started")
        val exception = repository.syncTasks().exceptionOrNull()
        return when (exception) {
            null -> Result.success()
            is BackendException, is IOException -> Result.retry()
            else -> Result.failure()
        }
    }
}

private const val TAG = "SyncWorker"
