import java.util.Properties
import java.io.FileInputStream

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

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
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

val composeCompilerVersion: String by project

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "jp.co.soramitsu.card"
    compileSdk = 34

    defaultConfig {
        applicationId = "jp.co.soramitsu.card"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":oauth"))
    implementation(libs.material)
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)

    implementation(libs.hiltandroid)
    kapt(libs.hiltcompiler)

    implementation(libs.soramitsu.uicore)
}

kapt {
    correctErrorTypes = true
}
