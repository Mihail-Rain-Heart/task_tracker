package com.task.tracker.core.database.utils

import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.task.tracker.core.database.Database
import com.task.tracker.core.database.utils.Consts.DB_TAG

internal object DatabaseCallbackLog : AndroidSqliteDriver.Callback(Database.Schema) {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.i(DB_TAG, "Database are create")
    }

    override fun onUpgrade(
        db: SupportSQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        super.onUpgrade(db, oldVersion, newVersion)
        Log.i(DB_TAG, "Database are upgrade")
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        Log.i(DB_TAG, "Database are open")
    }

    override fun onCorruption(db: SupportSQLiteDatabase) {
        super.onCorruption(db)
        Log.w(DB_TAG, "Database are corrupted")
    }
}
