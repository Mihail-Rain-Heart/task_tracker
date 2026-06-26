package com.task.tracker.core.network.di

import android.content.Context
import com.task.tracker.core.common.di.Fake
import com.task.tracker.core.common.network.Dispatcher
import com.task.tracker.core.common.network.Dispatchers
import com.task.tracker.core.network.fake.FakeWebServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FakeNetworkModule {

    @[Provides Singleton Fake]
    fun provideFakeWebServer(
        json: Json,
        @ApplicationContext context: Context,
        @Dispatcher(dispatcher = Dispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): FakeWebServer = FakeWebServer(
        context = context,
        ioDispatcher = ioDispatcher,
        json = json,
    )
}
