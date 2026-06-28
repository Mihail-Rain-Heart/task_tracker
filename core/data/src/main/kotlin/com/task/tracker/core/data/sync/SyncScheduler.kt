package com.task.tracker.core.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.task.tracker.core.common.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SyncScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val logger: Logger,
) {

    fun schedule() {
        logger.i(TAG, "Start schedule")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
        logger.i(TAG, "Request send to enqueue")
    }

    companion object {

        const val WORK_NAME = "com.task.tracker.core.data.sync.SyncScheduler"

        const val TAG = "SyncScheduler"
    }
}
