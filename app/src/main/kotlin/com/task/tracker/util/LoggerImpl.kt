package com.task.tracker.util

import android.util.Log
import com.task.tracker.core.common.util.Logger
import javax.inject.Inject

class LoggerImpl @Inject constructor(): Logger {

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
}
