package com.task.tracker.core.database.di

import com.task.tracker.core.database.data.tasks.TasksLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.task.tracker.core.database.data.tasks.TasksLocalDataSourceImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SourcesModule {

    @[Binds Singleton]
    abstract fun bindTasksLocalDataSource(impl: TasksLocalDataSourceImpl): TasksLocalDataSource
}
