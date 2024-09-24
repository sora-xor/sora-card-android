import java.io.FileInputStream
import java.util.Properties

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
    namespace = "jp.co.soramitsu.sora.communitytesting"
    compileSdk = 34

    defaultConfig {
        applicationId = "jp.co.soramitsu.sora.communitytesting"
        minSdk = 26
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
            buildConfigField(
                "String",
                "SORA_CARD_API_KEY",
                maybeWrapQuotes(secret("SORA_CARD_API_KEY_PROD")!!),
            )
            buildConfigField(
                "String",
                "SORA_CARD_DOMAIN",
                maybeWrapQuotes(secret("SORA_CARD_DOMAIN_PROD")!!),
            )
            buildConfigField(
                "String",
                "SORA_CARD_KYC_ENDPOINT_URL",
                maybeWrapQuotes(secret("SORA_CARD_KYC_ENDPOINT_URL_PROD")!!),
            )
            buildConfigField(
                "String",
                "SORA_CARD_KYC_USERNAME",
                maybeWrapQuotes(secret("SORA_CARD_KYC_USERNAME_PROD")!!),
            )
            buildConfigField(
                "String",
                "SORA_CARD_KYC_PASSWORD",
                maybeWrapQuotes(secret("SORA_CARD_KYC_PASSWORD_PROD")!!),
            )

            buildConfigField(
                "String",
                "SORA_API_BASE_URL",
                maybeWrapQuotes(secret("SORA_BACKEND_RELEASE")!!),
            )

            buildConfigField(
                "String",
                "PLATFORM_ID",
                maybeWrapQuotes(secret("PLATFORM_ID_TEST")!!),
            )

            buildConfigField(
                "String",
                "RECAPTCHA_KEY",
                maybeWrapQuotes(secret("RECAPTCH_KEY_TEST")!!),
            )

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            buildConfigField(
                "String",
                "SORA_CARD_API_KEY",
                maybeWrapQuotes(secret("SORA_CARD_API_KEY_TEST")!!),
            )
            buildConfigField(
                "String",
                "SORA_CARD_DOMAIN",
                maybeWrapQuotes(secret("SORA_CARD_DOMAIN_TEST")!!),
            )
            buildConfigField(
                "String",
                "SORA_CARD_KYC_ENDPOINT_URL",
                maybeWrapQuotes(secret("SORA_CARD_KYC_ENDPOINT_URL_TEST")!!),
            )
            buildConfigField(
                "String",
                "SORA_CARD_KYC_USERNAME",
                maybeWrapQuotes(secret("SORA_CARD_KYC_USERNAME_TEST")!!),
            )
            buildConfigField(
                "String",
                "SORA_CARD_KYC_PASSWORD",
                maybeWrapQuotes(secret("SORA_CARD_KYC_PASSWORD_TEST")!!),
            )

            buildConfigField(
                "String",
                "SORA_API_BASE_URL",
                maybeWrapQuotes(secret("SORA_BACKEND_DEBUG")!!),
            )

            buildConfigField(
                "String",
                "PLATFORM_ID",
                maybeWrapQuotes(secret("PLATFORM_ID_TEST")!!),
            )

            buildConfigField(
                "String",
                "RECAPTCHA_KEY",
                maybeWrapQuotes(secret("RECAPTCH_KEY_TEST")!!),
            )

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties",
            )
        }
    }
}

dependencies {

    implementation(project(":oauth"))
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)
    implementation(libs.material)

    implementation(libs.soramitsu.xnetworking.lib)
    implementation(libs.kotlinx.serialization)

    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.compose.material)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)

    implementation(libs.hiltandroid)
    kapt(libs.hiltcompiler)

    implementation(libs.soramitsu.uicore)
}

kapt {
    correctErrorTypes = true
}
