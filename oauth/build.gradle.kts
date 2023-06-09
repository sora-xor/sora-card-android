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
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("maven-publish")
    kotlin("plugin.serialization")
}

val composeCompilerVersion by extra("1.4.6")
val uiCoreVersion by extra("0.0.68")
val hiltVersion by extra("2.45")
val pwOauthSdkVersion by extra("1.2.2")
val pwKycSdkVersion by extra("4.2.0")
val dataStoreVersion by extra("1.0.0")
val ktorVersion by extra("2.2.3")

android {
    namespace = "jp.co.soramitsu.oauth"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "API_BASE_URL",
                maybeWrapQuotes(secret("SORA_BACKEND_RELEASE")!!)
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "API_BASE_URL",
                maybeWrapQuotes(secret("SORA_BACKEND_DEBUG")!!)
            )
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    } 
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.fragment:fragment-ktx:1.5.7")

    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")

    implementation("jp.co.soramitsu:ui-core:$uiCoreVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.30.1")

    implementation( "com.google.dagger:hilt-android:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    "kapt"("com.google.dagger:hilt-compiler:$hiltVersion")

    implementation("com.paywings.oauth:android-sdk:$pwOauthSdkVersion") {
        exclude("com.android.support", "support-compat")
        exclude("com.android.support", "support-media-compat")
    }
    implementation("com.paywings.onboarding.kyc:android-sdk:$pwKycSdkVersion") {
        exclude("com.android.support", "support-compat")
        exclude("com.android.support", "support-media-compat")
    }

    implementation("androidx.datastore:datastore-preferences:$dataStoreVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    api("io.ktor:ktor-client-okhttp:$ktorVersion")

    debugImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    debugImplementation("junit:junit:4.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    implementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("io.mockk:mockk:1.13.5")
}

kapt {
    correctErrorTypes = true
}

val currentVersion by extra("0.0.43")
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "jp.co.soramitsu"
            artifactId = "android-sora-card"
            version = currentVersion

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
