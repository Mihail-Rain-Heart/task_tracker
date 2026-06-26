package com.task.tracker.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.task.tracker.core.data.util.NetworkMonitor
import com.task.tracker.core.designsystem.component.NoConnection
import com.task.tracker.core.ui.utils.LocalConnectivity
import com.task.tracker.feature.task.api.navigation.TaskNavKey
import com.task.tracker.feature.task.impl.navigation.taskEntry

@Composable
fun App(networkMonitor: NetworkMonitor) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
    ) { paddingValues ->
        val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle(initialValue = true)
        CompositionLocalProvider(
            LocalConnectivity provides isOnline,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .safeContentPadding()
                    .consumeWindowInsets(WindowInsets.safeContent)
            ) {
                val backStack = remember { mutableStateListOf<NavKey>(TaskNavKey) }
                NoConnection(isOffline = !LocalConnectivity.current)

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        taskEntry()
                    }
                )
            }
        }
    }
}
