package com.task.tracker.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
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
import com.task.tracker.core.common.network.NetworkMonitor
import com.task.tracker.core.designsystem.component.NoConnection
import com.task.tracker.core.designsystem.theme.dimensions
import com.task.tracker.core.ui.utils.LocalConnectivity
import com.task.tracker.feature.task.api.navigation.TaskNavKey
import com.task.tracker.feature.task.impl.navigation.taskEntry

@Composable
fun App(networkMonitor: NetworkMonitor) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.exclude(
                        WindowInsets.ime,
                    ),
                ),
                hostState = snackbarHostState
            )
        }
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
                Spacer(modifier = Modifier.size(size = MaterialTheme.dimensions.spacingXs))

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        taskEntry(showSnackBar = { snackbarHostState.showSnackbar(it) == ActionPerformed })
                    }
                )
            }
        }
    }
}
