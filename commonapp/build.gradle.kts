plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
    id("kotlin-parcelize")
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "jp.co.soramitsu.soracard.commonapp"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":oauth"))
    implementation(libs.soramitsu.xnetworking.lib)
    implementation(libs.kotlinx.serialization)

    implementation(libs.hiltandroid)
    kapt(libs.hiltcompiler)

    implementation(libs.sw.android.foundation)
}

kapt {
    correctErrorTypes = true
}
