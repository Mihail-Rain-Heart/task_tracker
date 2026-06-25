package com.task.tracker.core.network.fake

import android.content.Context
import com.task.tracker.core.common.network.Dispatchers
import com.task.tracker.core.network.fake.FakeWebServer.StatusCode.SERVICE_UNAVAILABLE
import com.task.tracker.core.network.model.NetworkError
import com.task.tracker.core.network.model.NetworkTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Clock
import com.task.tracker.core.common.network.Dispatcher as TDispatcher

/**
 * Подменяет реальный бекенд в приложении.
 * Живёт также долго как и процесс, поэтому не освобождаю.
 * Доступ к методам start и url у MockWebServer требует, чтобы обращение было не на Main-потоке. Из-за этого напрямую строю Url для retrofit.
 */
class FakeWebServer @Inject constructor(
    @ApplicationContext context: Context,
    @TDispatcher(dispatcher = Dispatchers.IO) ioDispatcher: CoroutineDispatcher,
    json: Json,
) {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val _isStarted = MutableStateFlow(value = false)

    val isStarted: StateFlow<Boolean> = _isStarted.asStateFlow()

    private val server = MockWebServer()
        .apply {
            val mockTasks = json.decodeFromString<List<NetworkTask>>(
                context
                    .assets
                    .open(MOCK_TASKS_FILE_NAME)
                    .bufferedReader()
                    .use { it.readText() }
            )

            dispatcher = FakeDispatcher(json = json, mockTasks = mockTasks)
        }

    init {
        scope.launch(ioDispatcher) {
            server.start()
            _isStarted.value = true
        }
    }

    fun getUrl(): String = server.url("/").toString()

    private class FakeDispatcher(
        private val json: Json,
        private val mockTasks: List<NetworkTask>,
    ) : Dispatcher() {

        // шум на основании интервала задержки ответа.
        // Можно и без шума и умножить этот интервал на 100, тогда это интервал задержки запроса.
        // Но я так не стал делать для того, чтобы влияние на ответ с ошибкой (503 код) выглядело более случайно чем (true/false).random().
        private val noise = 0..15

        override fun dispatch(request: RecordedRequest): MockResponse {
            val response = MockResponse()
                .setBodyDelay(getRandomDelayMs(), TimeUnit.MILLISECONDS)

            return when {
                isNeedError() -> response
                    .setResponseCode(SERVICE_UNAVAILABLE)
                    .setBody(EMPTY_BODY_JSON_STRING)

                request.method == Method.GET && request.path.equals(
                    other = ApiPath.TASKS,
                    ignoreCase = true
                ) -> response
                    .setResponseCode(StatusCode.OK)
                    .setBody(json.encodeToString(mockTasks))

                request.method == Method.PATCH && request.path?.startsWith(ApiPath.TASKS) == true -> {
                    val requestTask = json.decodeFromString<NetworkTask>(request.body.readUtf8())
                    response
                        .setResponseCode(StatusCode.OK)
                        // не стал мудрить. Всегда считаю, что если мой запрос получил 200, значит та задача статус которой я переводил на беке успешно переведена.
                        .setBody(
                            json.encodeToString(
                                value = requestTask.copy(
                                    updatedAt = Clock.System.now()
                                )
                            )
                        )
                }

                else -> response
                    .setResponseCode(StatusCode.NOT_FOUND)
                    .setBody(
                        json.encodeToString(
                            value = NetworkError(
                                message = "Not found api for method(=${request.method}) and path(=${request.path})"
                            )
                        )
                    )
            }
        }

        private fun isNeedError(): Boolean {
            // условие "случайности", от себя взял четность, так как по условию это не оговорено, а задачка тестовая, нет смысла уточнять.
            return noise.random() % 2 == 0
        }

        private fun getRandomDelayMs(): Long {
            return noise.random().coerceAtLeast(minimumValue = 5) * 100L
        }
    }

    private object Method {

        const val GET = "GET"
        const val PATCH = "PATCH"
    }

    private object ApiPath {

        const val TASKS = "/tasks"
    }

    private object StatusCode {

        const val OK = 200

        const val NOT_FOUND = 404

        const val SERVICE_UNAVAILABLE = 503
    }
}

private const val EMPTY_BODY_JSON_STRING = "{}"

private const val MOCK_TASKS_FILE_NAME = "tasks.json"
