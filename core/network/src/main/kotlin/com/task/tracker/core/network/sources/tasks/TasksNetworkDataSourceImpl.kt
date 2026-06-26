package com.task.tracker.core.network.sources.tasks

import com.task.tracker.core.common.di.Fake
import com.task.tracker.core.network.api.TASKS_URL
import com.task.tracker.core.network.api.TasksApi
import com.task.tracker.core.network.fake.FakeWebServer
import com.task.tracker.core.network.model.ApiResult
import com.task.tracker.core.network.model.NetworkTask
import com.task.tracker.core.network.utils.safeCall
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

internal class TasksNetworkDataSourceImpl @Inject constructor(
    private val api: TasksApi,
    @property:Fake private val fakeServer: FakeWebServer, // пришлось строить url, потому что доступ к url и start сервера не должен быть на Main-потоке
) : TasksNetworkDataSource {

    override val isFakeWebServerStarted: StateFlow<Boolean> get() = fakeServer.isStarted

    override suspend fun getTasks(): ApiResult<List<NetworkTask>> {
        return safeCall {
            api.getTasks(url = buildUrl(url = TASKS_URL))
        }
    }

    override suspend fun updateStatus(
        id: Long,
        task: NetworkTask
    ): ApiResult<NetworkTask> {
        return safeCall {
            api.updateStatus(
                // id = id, // при боевом сервере было бы так
                body = task,
                url = buildUrl(url = "$TASKS_URL/$id")
            )
        }
    }

    private fun buildUrl(url: String): String = "${fakeServer.getUrl()}$url"
}
