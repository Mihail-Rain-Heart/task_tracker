plugins {
    alias(libs.plugins.tasktracker.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.task.tracker.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.javax.inject)
}
