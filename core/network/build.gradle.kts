plugins {
    alias(libs.plugins.tasktracker.android.library)
    alias(libs.plugins.tasktracker.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    buildFeatures {
        buildConfig = true
    }

    namespace = "com.task.tracker.core.network"
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)
    api(libs.kotlinx.datetime)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.okhttp.mockServer)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
}
