package com.task.tracker.core.network.sources.tasks

import com.task.tracker.core.network.model.ApiResult
import com.task.tracker.core.network.model.NetworkTask
import kotlinx.coroutines.flow.StateFlow

interface TasksNetworkDataSource {

    val isFakeWebServerStarted: StateFlow<Boolean>

    suspend fun getTasks(): ApiResult<List<NetworkTask>>

    suspend fun updateStatus(id: Long, task: NetworkTask): ApiResult<NetworkTask>
}
