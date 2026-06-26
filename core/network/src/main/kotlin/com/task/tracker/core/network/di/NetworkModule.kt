package com.task.tracker.core.network.di

import com.task.tracker.core.network.BuildConfig
import com.task.tracker.core.network.api.TasksApi
import com.task.tracker.core.network.utils.InstantMillisSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton
import kotlin.time.Instant

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @[Provides Singleton]
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true

        serializersModule = SerializersModule {
            contextual(
                Instant::class,
                InstantMillisSerializer
            )
        }
    }

    @[Provides Singleton]
    fun okHttpCallFactory(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        } else {
                            setLevel(HttpLoggingInterceptor.Level.NONE)
                        }
                    },
            )
            .build()
    }

    @[Provides Singleton]
    fun provideBaseRetrofit(
        networkJson: Json,
        okhttpCallFactory: dagger.Lazy<OkHttpClient>,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(EMPTY_BASE_URL)
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory(contentType = CONTENT_TYPE.toMediaType()),
            )
            .build()
    }

    @[Provides Singleton]
    fun provideTasksApi(baseClient: Retrofit): TasksApi {
        return baseClient.newBuilder()
            .build()
            .create(TasksApi::class.java)
    }
}

private const val CONTENT_TYPE = "application/json"

private const val EMPTY_BASE_URL = "https://api.com"
