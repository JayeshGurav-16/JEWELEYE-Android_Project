android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    compileSdk = 35
    buildToolsVersion = "35.0.0"
    defaultConfig {
        applicationId = "com.example.store4"
        versionCode = 1
        versionName = "1.0"
        targetSdk = 35
        minSdk = 26
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply true
}

