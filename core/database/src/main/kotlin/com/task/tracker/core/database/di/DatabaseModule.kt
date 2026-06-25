package com.task.tracker.core.database.di

import android.content.Context
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.task.tracker.core.database.Database
import com.task.tracker.core.database.Task
import com.task.tracker.core.database.adapter.InstantAdapter
import com.task.tracker.core.database.utils.DatabaseCallbackLog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideDriver(@ApplicationContext context: Context): AndroidSqliteDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = DB_NAME,
            callback = DatabaseCallbackLog
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(driver: AndroidSqliteDriver): Database {
        return Database(
            driver = driver,
            taskAdapter = Task.Adapter(
                statusAdapter = EnumColumnAdapter(),
                updatedAtAdapter = InstantAdapter()
            ),
        )
    }
}

private const val DB_NAME = "task_database.db"
