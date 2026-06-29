package com.task.tracker.core.data.di

import com.task.tracker.core.data.repository.tasks.TasksRepository
import com.task.tracker.core.data.repository.tasks.TasksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @[Binds Singleton]
    abstract fun bindTasksRepository(impl: TasksRepositoryImpl): TasksRepository
}
