plugins {
    alias(libs.plugins.tasktracker.android.library)
    alias(libs.plugins.tasktracker.hilt)
    alias(libs.plugins.tasktracker.sqldelight)
}

android {
    namespace = "com.task.tracker.core.database"

    sourceSets {
        getByName("main") {
            java.directories.add("build/generated/sqldelight/code/Database/debug")
        }
    }
}

dependencies {
    api(projects.core.model)
}
