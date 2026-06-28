package com.task.tracker

import androidx.work.Configuration
import com.task.tracker.core.data.sync.SyncScheduler
import com.task.tracker.core.data.sync.SyncWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import android.app.Application as _Application

@HiltAndroidApp
class Application : _Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: SyncWorkerFactory

    @Inject
    lateinit var syncScheduler: SyncScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        syncScheduler.schedule()
    }
}
