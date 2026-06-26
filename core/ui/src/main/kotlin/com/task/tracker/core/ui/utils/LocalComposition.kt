package com.task.tracker.core.ui.utils

import androidx.compose.runtime.staticCompositionLocalOf

val LocalConnectivity = staticCompositionLocalOf<Boolean> {
    error("LocalConnectivity should be initialized")
}
