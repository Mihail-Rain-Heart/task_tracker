package com.task.tracker.di

import com.task.tracker.core.common.util.Logger
import com.task.tracker.util.LoggerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CommonModule {

    @[Binds Singleton]
    fun bindLogger(impl: LoggerImpl): Logger
}
