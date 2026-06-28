package com.task.tracker.core.domain

import com.task.tracker.core.data.repository.tasks.TasksRepository
import com.task.tracker.core.data.sync.SyncScheduler
import com.task.tracker.core.model.TaskStatus
import javax.inject.Inject
import kotlin.time.Instant

class UpdateTaskStatusUseCase @Inject constructor(
    private val repository: TasksRepository,
    private val syncScheduler: SyncScheduler
) {

    suspend operator fun invoke(
        id: Long,
        status: TaskStatus,
        updatedAt: Instant,
    ): Result<Unit> = repository.updateStatus(
        id = id,
        status = status,
        updatedAt = updatedAt,
    ).also {
        syncScheduler.schedule()
    }
}
