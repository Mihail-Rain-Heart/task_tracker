package com.task.tracker.core.ui.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun Instant.getTimeInStringDefault(): String {
    return defaultTimeFormatter.format(toJavaInstant())
}

private val defaultTimeFormatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm")
    .withZone(ZoneId.systemDefault())
