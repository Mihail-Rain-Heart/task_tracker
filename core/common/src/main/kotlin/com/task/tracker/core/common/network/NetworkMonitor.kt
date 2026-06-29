package com.task.tracker.core.common.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {

    val isOnline: StateFlow<Boolean>

    val isServerAvailable: StateFlow<Boolean>
}
