package com.task.tracker.core.network.model

import android.annotation.SuppressLint
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NetworkTask(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String? = null,
    @SerialName("status")
    val status: NetworkTaskStatus,
    @SerialName("updated_at")
    val updatedAt: @Contextual Instant,
)
