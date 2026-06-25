package com.task.tracker.core.network.model

sealed interface ApiResult<out T> {

    data class Success<T>(val data: T) : ApiResult<T>

    data class NetworkError(val throwable: Throwable) : ApiResult<Nothing>
}
