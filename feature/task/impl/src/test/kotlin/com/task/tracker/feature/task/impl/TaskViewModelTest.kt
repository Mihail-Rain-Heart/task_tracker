package com.task.tracker.feature.task.impl

import app.cash.turbine.test
import com.task.tracker.core.domain.ObserveTasksUseCase
import com.task.tracker.core.domain.RefreshTasksUseCase
import com.task.tracker.core.domain.SyncTaskUseCase
import com.task.tracker.core.domain.UpdateTaskStatusUseCase
import com.task.tracker.core.model.Task
import com.task.tracker.core.model.TaskStatus
import com.task.tracker.feature.task.impl.presentation.tasks.TaskAction
import com.task.tracker.feature.task.impl.presentation.tasks.TaskViewModel
import com.task.tracker.feature.task.impl.presentation.tasks.model.toDomain
import com.task.tracker.feature.task.impl.presentation.tasks.model.toUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val observeTaskUseCase = mockk<ObserveTasksUseCase>()
    private val refreshTaskUseCase = mockk<RefreshTasksUseCase>()
    private val updateTaskUseCase = mockk<UpdateTaskStatusUseCase>()
    private val syncTaskUseCase = mockk<SyncTaskUseCase>()

    private lateinit var viewModel: TaskViewModel

    lateinit var dispatcher: TestDispatcher

    @Before
    fun setup() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        every {
            observeTaskUseCase()
        } returns flowOf(
            listOf(
                Task(
                    id = 1,
                    title = "",
                    isSyncing = false,
                    isSyncRequired = false,
                    updatedAt = Clock.System.now(),
                    status = TaskStatus.DONE
                )
            )
        )

        coEvery { refreshTaskUseCase() } returns Result.success(Unit)

        viewModel = TaskViewModel(
            logger = mockk(relaxed = true),
            observeTaskUseCase = observeTaskUseCase,
            refreshTaskUseCase = refreshTaskUseCase,
            updateTaskUseCase = updateTaskUseCase,
            syncTaskUseCase = syncTaskUseCase,
        )
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `when tasks emitted state updated`() = runTest {
        viewModel.state.test {

            val state = awaitItem()

            assertEquals(
                TaskStatus.DONE,
                state.tasks.first().status.toDomain()
            )
        }
    }

    @Test
    fun `when update action emitted use case is called`() = runTest {

        val updatedAt = Clock.System.now()

        coEvery {
            updateTaskUseCase(
                id = any(),
                status = any(),
                updatedAt = any(),
            )
        } returns Result.success(Unit)

        viewModel.handle(
            TaskAction.OnTaskClicked(
                id = 1,
                status = TaskStatus.TODO.toUiModel(),
                updatedAt = updatedAt
            )
        )

        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            updateTaskUseCase(
                id = 1,
                status = TaskStatus.TODO,
                updatedAt = updatedAt
            )
        }
    }
}
