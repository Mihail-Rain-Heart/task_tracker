plugins {
    alias(libs.plugins.tasktracker.jvm.library)
    alias(libs.plugins.tasktracker.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.viewModelCompose)
}
