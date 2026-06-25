package com.task.tracker.core.network.di

import com.task.tracker.core.network.sources.tasks.TasksNetworkDataSource
import com.task.tracker.core.network.sources.tasks.TasksNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourceModule {

    @Binds
    fun bindTasksNetworkDataSource(impl: TasksNetworkDataSourceImpl): TasksNetworkDataSource
}
