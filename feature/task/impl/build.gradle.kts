plugins {
    alias(libs.plugins.tasktracker.android.feature.impl)
    alias(libs.plugins.tasktracker.android.library.compose)
}

android {
    namespace = "com.task.tracker.feature.task.impl"
}

dependencies {
    implementation(projects.feature.task.api)

    implementation(projects.core.domain)
    implementation(projects.core.data)

    // tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mock)
    testImplementation(libs.turbine)
}
