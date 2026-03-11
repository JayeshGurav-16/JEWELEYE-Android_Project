plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")


}

android {
    namespace = "com.example.admin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.admin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    //noinspection UseTomlInstead
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.tasks)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.preference)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.gorisse.thomas.sceneform:sceneform:1.23.0")

}