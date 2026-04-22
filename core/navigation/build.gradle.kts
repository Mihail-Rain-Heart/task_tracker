plugins {
    alias(libs.plugins.tasktracker.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.tasktracker.hilt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.task.tracker.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.savedstate.compose)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
}
