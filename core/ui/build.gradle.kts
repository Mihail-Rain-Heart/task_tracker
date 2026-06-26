plugins {
    alias(libs.plugins.tasktracker.android.library)
    alias(libs.plugins.tasktracker.android.library.compose)
}

android {
    namespace = "com.task.tracker.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)
}
