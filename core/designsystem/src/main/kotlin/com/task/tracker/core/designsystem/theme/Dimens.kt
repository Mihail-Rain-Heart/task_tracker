package com.task.tracker.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppDimens(
    val spacingXs: Dp,
    val spacingSm: Dp,
    val spacingMd: Dp,
    val spacingLg: Dp,
)

val LocalDimens = staticCompositionLocalOf { DefaultDimens }

val MaterialTheme.dimensions: AppDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalDimens.current

val DefaultDimens = AppDimens(
    spacingXs = 4.dp,
    spacingSm = 8.dp,
    spacingMd = 16.dp,
    spacingLg = 24.dp,
)
