package com.task.tracker.core.data.util

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {

    val isOnline: Flow<Boolean>
}
