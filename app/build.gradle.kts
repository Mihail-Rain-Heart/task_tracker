plugins {
    alias(libs.plugins.tasktracker.android.application)
    alias(libs.plugins.tasktracker.android.application.compose)
    alias(libs.plugins.tasktracker.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.task.tracker"

    defaultConfig {
        applicationId = "com.task.tracker"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        getByName("main").java.directories.add("src/main/kotlin")
        getByName("test").java.directories.add("src/test/kotlin")
        getByName("androidTest").java.directories.add("src/androidTest/kotlin")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.database)

    implementation(projects.feature.task.api)
    implementation(projects.feature.task.impl)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
    implementation(libs.kotlinx.serialization.json)

    ksp(libs.hilt.compiler)

    // Worker
    implementation(libs.workManager)
    implementation(libs.hilt.work)
}
