package com.task.tracker.core.network.api

import com.task.tracker.core.network.model.NetworkTask
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * Url перезаписывается. Причину см. в [com.task.tracker.core.network.fake.FakeWebServer]
 */
interface TasksApi {

    // Ясно, что тут url в конце (как и в методах ниже) - необходимость из-за fake server-а, а так если эту часть удалить, вполне себе REST api в закоменченной части
    //@GET("/$TASKS_URL")
    @GET
    suspend fun getTasks(@Url url: String): Response<List<NetworkTask>>

    //@PATCH("/$TASKS_URL/{id}")
    @PATCH
    suspend fun updateStatus(
        //@Path("id") id: Long,
        @Body body: NetworkTask,
        @Url url: String
    ): Response<NetworkTask>
}

const val TASKS_URL = "tasks"
