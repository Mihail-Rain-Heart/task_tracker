package com.task.tracker.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NetworkTaskStatus {

    @SerialName("todo")
    TODO,

    @SerialName("in_progress")
    IN_PROGRESS,

    @SerialName("done")
    DONE;
}
