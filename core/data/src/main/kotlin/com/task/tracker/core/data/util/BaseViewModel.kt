package com.task.tracker.core.data.util

import android.util.Log
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    init {
        Log.i(TAG, "init ${this::class.simpleName}")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "cleared ${this::class.simpleName}")
    }
}

private const val TAG = "VM"
