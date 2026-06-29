package com.task.tracker.core.common.extension

import kotlinx.coroutines.CancellationException

suspend inline fun <T> runCatchingCancellable(crossinline callback: suspend () -> T): Result<T> {
    return try {
        Result.success(value = callback())
    } catch (throwable: Throwable) {
        if (throwable is CancellationException) {
            throw throwable
        }
        Result.failure(exception = throwable)
    }
}
