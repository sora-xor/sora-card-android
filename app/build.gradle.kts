import java.util.Properties
import java.io.FileInputStream

fun secret(name: String): String? {
    val fileProperties = File(rootProject.projectDir.absolutePath, "local.properties")
    val pr = runCatching { FileInputStream(fileProperties) }.getOrNull()?.let { file ->
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
    compileSdk = 34

    defaultConfig {
        applicationId = "jp.co.soramitsu.card"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            buildConfigField("String", "SORA_CARD_API_KEY", maybeWrapQuotes(secret("SORA_CARD_API_KEY_PROD")!!))
            buildConfigField("String", "SORA_CARD_DOMAIN", maybeWrapQuotes(secret("SORA_CARD_DOMAIN_PROD")!!))
            buildConfigField("String", "SORA_CARD_KYC_ENDPOINT_URL", maybeWrapQuotes(secret("SORA_CARD_KYC_ENDPOINT_URL_PROD")!!))
            buildConfigField("String", "SORA_CARD_KYC_USERNAME", maybeWrapQuotes(secret("SORA_CARD_KYC_USERNAME_PROD")!!))
            buildConfigField("String", "SORA_CARD_KYC_PASSWORD", maybeWrapQuotes(secret("SORA_CARD_KYC_PASSWORD_PROD")!!))

            buildConfigField("String","SORA_API_BASE_URL",maybeWrapQuotes(secret("SORA_BACKEND_RELEASE")!!))

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            buildConfigField("String", "SORA_CARD_API_KEY", maybeWrapQuotes(secret("SORA_CARD_API_KEY_TEST")!!))
            buildConfigField("String", "SORA_CARD_DOMAIN", maybeWrapQuotes(secret("SORA_CARD_DOMAIN_TEST")!!))
            buildConfigField("String", "SORA_CARD_KYC_ENDPOINT_URL", maybeWrapQuotes(secret("SORA_CARD_KYC_ENDPOINT_URL_TEST")!!))
            buildConfigField("String", "SORA_CARD_KYC_USERNAME", maybeWrapQuotes(secret("SORA_CARD_KYC_USERNAME_TEST")!!))
            buildConfigField("String", "SORA_CARD_KYC_PASSWORD", maybeWrapQuotes(secret("SORA_CARD_KYC_PASSWORD_TEST")!!))

            buildConfigField("String","SORA_API_BASE_URL",maybeWrapQuotes(secret("SORA_BACKEND_DEBUG")!!))

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
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.material:material:1.5.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")

    implementation( "com.google.dagger:hilt-android:2.47")
    "kapt"("com.google.dagger:hilt-compiler:2.47")

    implementation("jp.co.soramitsu:ui-core:0.1.0")
}

kapt {
    correctErrorTypes = true
}
