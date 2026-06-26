package com.task.tracker.feature.task.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.task.tracker.feature.task.api.navigation.TaskNavKey
import com.task.tracker.feature.task.impl.presentation.tasks.TaskScreen

fun EntryProviderScope<NavKey>.taskEntry() {
    entry<TaskNavKey> {
        TaskScreen()
    }
}
