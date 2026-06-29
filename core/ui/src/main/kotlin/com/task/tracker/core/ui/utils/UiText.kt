package com.task.tracker.core.ui.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {

    data class Resource(@get:StringRes val resId: Int) : UiText

    data class Value(val value: String) : UiText

    @Composable
    fun stringValue(): String = when(this) {
        is Resource -> stringResource(id = resId)
        is Value -> value
    }
}
