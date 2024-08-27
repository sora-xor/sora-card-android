import java.io.FileInputStream
import java.util.Properties
import org.gradle.kotlin.dsl.kapt

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
    alias(libs.plugins.kover)
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
        minSdk = 26
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
    packaging {
        resources {
            excludes += listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
            )
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)
    implementation(libs.material)
    implementation(libs.compose.navigation)

    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.compose.material)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.ui.tooling)

    implementation(libs.sw.android.foundation)
    implementation(libs.soramitsu.uicore)
    implementation(libs.kotlinx.serialization)
    implementation(libs.datastore)

    implementation(libs.hiltandroid)
    implementation(libs.hiltnavigationcompose)
    kapt(libs.hiltcompiler)

    implementation(libs.pwoauth)
    implementation(libs.pwkyc) {
        exclude(module = "idensic-mobile-sdk-videoident")
//        exclude(module = "idensic-mobile-sdk-internal-core")
//        exclude(module = "idensic-mobile-sdk-internal")
    }

    implementation(libs.coroutine.core)

    testImplementation(libs.tests.archcore)
    testImplementation(libs.tests.junit)
    testImplementation(libs.tests.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.ui.test.junit4)
}

kapt {
    correctErrorTypes = true
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "jp.co.soramitsu"
            artifactId = "android-sora-card"
            version = "1.1.17"

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
            url = uri(project.layout.buildDirectory.dir("scnrepo").get().asFile.path)
        }
    }
}

kover {
    useJacoco()
}

koverReport {
    androidReports("release") {
        filters {
            excludes {
                classes(
                    "**.di.*",
                )
            }
        }
        xml {
            onCheck = true
            setReportFile(file("${project.rootDir}/report/coverage.xml"))
        }
        html {
            onCheck = true
        }
        verify {
            onCheck = true
            rule {
                isEnabled = true

                minBound(20)
                // TODO: Update to 85
            }
        }
    }
}
