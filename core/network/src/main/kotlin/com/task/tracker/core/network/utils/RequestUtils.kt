package com.task.tracker.core.network.utils

import com.task.tracker.core.network.model.ApiResult
import kotlinx.coroutines.delay
import retrofit2.Response
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

suspend fun <T> safeCall(call: suspend () -> Response<T>): ApiResult<T> {
    return retry(
        times = MAX_RETRY_COUNT,
        predicate = ::shouldRetry,
    ) {
        val response = call()

        if (response.isSuccessful) {
            val body = response.body()

            if (body != null) {
                ApiResult.Success(data = body)
            } else {
                ApiResult.NetworkError(throwable = IllegalStateException("Response body is null"))
            }

        } else {
            val code = response.code()
            val message =
                "Network error code(=$code) message(=${response.message()}) body(=${response.body()})"
            if (code in 500..599) {
                throw BackendException(message)
            } else {
                ApiResult.NetworkError(throwable = Error(message))
            }
        }
    }
}

private suspend fun <T> retry(
    times: Int,
    predicate: (Throwable) -> Boolean,
    block: suspend () -> T
): T {

    var attempt = 0

    while (true) {
        try {
            return block()
        } catch (e: Throwable) {
            attempt++

            if (attempt > times || !predicate(e)) {
                throw e
            }

            delay((1000L * attempt).milliseconds)
        }
    }
}

private fun shouldRetry(throwable: Throwable): Boolean {
    return when (throwable) {
        is IOException,
        is BackendException -> true

        else -> false
    }
}

private class BackendException(message: String) : Exception(message)

private const val MAX_RETRY_COUNT = 2
