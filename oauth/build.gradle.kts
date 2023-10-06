import org.gradle.kotlin.dsl.kapt
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
    id("maven-publish")
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
    id("kotlin-parcelize")
}

val composeCompilerVersion: String by project

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "jp.co.soramitsu.oauth"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        multiDexEnabled = true

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
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        targetSdk = 34
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.appcompat)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.compose.navigation)

    implementation(libs.soramitsu.uicore)
    implementation(libs.soramitsu.xnetworking.basic)
    implementation(libs.ktor)
    implementation(libs.kotlinx.serialization)
    implementation(libs.datastore)

    implementation(libs.hiltandroid)
    implementation(libs.hiltnavigationcompose)
    kapt(libs.hiltcompiler)

    implementation(libs.pwoauth) {
        exclude("com.android.support", "support-compat")
        exclude("com.android.support", "support-media-compat")
    }
    implementation(libs.pwkyc) {
        exclude("com.android.support", "support-compat")
        exclude("com.android.support", "support-media-compat")
        exclude(module = "pinview")
    }
    implementation(libs.pinview)

    implementation(libs.coroutine.core)

    testImplementation(libs.tests.archcore)
    testImplementation(libs.tests.junit)
    testImplementation(libs.tests.mockk)
    testImplementation(libs.coroutine.test)
}

kapt {
    correctErrorTypes = true
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "jp.co.soramitsu"
            artifactId = "android-sora-card"
            version = "0.1.52"

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "scnRepo"
            url = uri(secret("RELEASE_REPOSITORY_URL")!!)
            credentials {
                username = secret("NEXUS_USERNAME")
                password = secret("NEXUS_PASSWORD")
            }
        }
        maven {
            name = "scnRepoLocal"
            url = uri("${project.buildDir}/scnrepo")
        }
    }
}
