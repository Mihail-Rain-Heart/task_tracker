plugins {
    alias(libs.plugins.tasktracker.jvm.library)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.viewModelCompose)
}
