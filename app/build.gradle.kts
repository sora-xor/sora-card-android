import java.util.Properties
import java.io.FileInputStream

fun secret(name: String): String? {
    val pr = runCatching { FileInputStream("local.properties") }.getOrNull()?.let { file ->
        Properties().apply {
            load(file)
        }
    }
    return pr?.getProperty(name) ?: System.getenv(name)
}

fun maybeWrapQuotes(s: String): String {
    return if (s.startsWith("\"")) s else "\"" + s + "\""
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "jp.co.soramitsu.card"
    compileSdk = 33

    defaultConfig {
        applicationId = "jp.co.soramitsu.card"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SORA_CARD_API_KEY", maybeWrapQuotes(secret("SORA_CARD_API_KEY")!!))
        buildConfigField("String", "SORA_CARD_DOMAIN", maybeWrapQuotes(secret("SORA_CARD_DOMAIN")!!))
        buildConfigField("String", "SORA_CARD_KYC_ENDPOINT_URL", maybeWrapQuotes(secret("SORA_CARD_KYC_ENDPOINT_URL")!!))
        buildConfigField("String", "SORA_CARD_KYC_USERNAME", maybeWrapQuotes(secret("SORA_CARD_KYC_USERNAME")!!))
        buildConfigField("String", "SORA_CARD_KYC_PASSWORD", maybeWrapQuotes(secret("SORA_CARD_KYC_PASSWORD")!!))
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }
}

dependencies {

    implementation(project(":oauth"))
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.material:material:1.4.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.2")

    implementation( "com.google.dagger:hilt-android:2.45")
    "kapt"("com.google.dagger:hilt-compiler:2.45")

    implementation("jp.co.soramitsu:ui-core:0.0.64")
}

kapt {
    correctErrorTypes = true
}