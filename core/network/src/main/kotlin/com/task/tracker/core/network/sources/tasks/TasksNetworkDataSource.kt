package com.task.tracker.core.network.sources.tasks

import com.task.tracker.core.network.model.ApiResult
import com.task.tracker.core.network.model.NetworkTask

interface TasksNetworkDataSource {

    suspend fun getTasks(): ApiResult<List<NetworkTask>>

    suspend fun updateStatus(id: Long, task: NetworkTask): ApiResult<NetworkTask>
}
