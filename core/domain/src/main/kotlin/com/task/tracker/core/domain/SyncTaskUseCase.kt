package com.task.tracker.core.domain

import com.task.tracker.core.data.repository.tasks.TasksRepository
import javax.inject.Inject
import kotlin.time.Instant

class SyncTaskUseCase @Inject constructor(
    private val repository: TasksRepository,
) {

    suspend operator fun invoke(id: Long, updatedAt: Instant): Result<Unit> {
        return repository.syncTask(id = id, updatedAt = updatedAt)
    }
}
