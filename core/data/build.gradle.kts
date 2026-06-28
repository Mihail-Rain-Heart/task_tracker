plugins {
    alias(libs.plugins.tasktracker.android.library)
    alias(libs.plugins.tasktracker.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.task.tracker.core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.network)
    api(projects.core.database)

    implementation(libs.workManager)
    implementation(libs.hilt.work)
}
