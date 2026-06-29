package com.task.tracker.core.data

import com.task.tracker.core.common.network.NetworkMonitor
import com.task.tracker.core.data.mapper.toNetwork
import com.task.tracker.core.data.repository.tasks.TasksRepositoryImpl
import com.task.tracker.core.database.Task
import com.task.tracker.core.database.data.sources.tasks.TasksLocalDataSource
import com.task.tracker.core.model.TaskStatus
import com.task.tracker.core.network.model.ApiResult
import com.task.tracker.core.network.model.NetworkTask
import com.task.tracker.core.network.model.NetworkTaskStatus
import com.task.tracker.core.network.sources.tasks.TasksNetworkDataSource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class TasksRepositoryTest {

    private val ioDispatcher = StandardTestDispatcher()

    private val networkMonitor = mockk<NetworkMonitor>()
    private val local = mockk<TasksLocalDataSource>(relaxed = true)
    private val network = mockk<TasksNetworkDataSource>(relaxed = true)

    private lateinit var repository: TasksRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(ioDispatcher)

        every { networkMonitor.isOnline } returns MutableStateFlow(true)
        every { networkMonitor.isServerAvailable } returns MutableStateFlow(true)

        repository = TasksRepositoryImpl(
            ioDispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            localTasks = local,
            networkTasks = network
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getTasks saves network data to local`() = runTest {
        val now = Clock.System.now()

        coEvery { network.getTasks() } returns ApiResult.Success(
            listOf(
                NetworkTask(
                    id = 1,
                    title = "test",
                    status = NetworkTaskStatus.TODO,
                    updatedAt = now
                )
            )
        )

        coEvery { local.setTask(any()) } just Runs
        coEvery { local.getTaskById(any()) } returns null
        coEvery { local.getSyncRequiredTasks() } returns emptyList()

        repository.getTasks()

        advanceUntilIdle()

        coVerify(atLeast = 1) {
            local.setTask(any())
        }
    }

    @Test
    fun `updateStatus does not update when status is already the same`() = runTest {
        val now = Clock.System.now()

        val existingTask = Task(
            id = 1L,
            title = "test",
            status = TaskStatus.IN_PROGRESS,
            updatedAt = now,
            isSyncRequired = false,
            isSyncing = false
        )

        coEvery { local.getTaskById(1L) } returns existingTask

        coEvery { local.setTask(any()) } just Runs
        coEvery { network.updateStatus(any(), any()) } returns ApiResult.Success(
            existingTask.toNetwork()
        )

        val result = repository.updateStatus(
            id = 1L,
            status = TaskStatus.IN_PROGRESS,
            updatedAt = now
        )

        advanceUntilIdle()

        assertTrue(result.isSuccess)

        coVerify(exactly = 0) {
            network.updateStatus(any(), any())
        }

        coVerify(exactly = 0) {
            local.setTask(match { it.isSyncRequired })
        }
    }

    @Test
    fun `updateStatus does update when status is not the same`() = runTest {
        val now = Clock.System.now()

        val existingTask = Task(
            id = 1L,
            title = "test",
            status = TaskStatus.IN_PROGRESS,
            updatedAt = now,
            isSyncRequired = false,
            isSyncing = false
        )

        coEvery { local.getTaskById(1L) } returns existingTask

        coEvery { local.setTask(any()) } just Runs
        coEvery { network.updateStatus(any(), any()) } returns ApiResult.Success(
            existingTask.toNetwork()
        )

        val result = repository.updateStatus(
            id = 1L,
            status = TaskStatus.DONE,
            updatedAt = now
        )

        advanceUntilIdle()

        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            network.updateStatus(any(), any())
        }

        coVerify(exactly = 1) {
            local.setTask(match { !it.isSyncRequired })
        }
    }
}