import java.io.FileInputStream
import java.util.Properties

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

fun secret(name: String): String? {
    val fileProperties = File(rootProject.projectDir.absolutePath, "local.properties")
    val pr = runCatching { FileInputStream(fileProperties) }.getOrNull()?.let { file ->
        Properties().apply {
            load(file)
        }
    }
    return pr?.getProperty(name) ?: System.getenv(name)
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri(secret("PAY_WINGS_REPOSITORY_URL")!!)
            credentials {
                username = secret("PAY_WINGS_USERNAME")
                password = secret("PAY_WINGS_PASSWORD")
            }
        }

        mavenLocal()
        maven {
            url = uri(secret("RELEASE_REPOSITORY_URL")!!)
        }
    }
}

rootProject.name = "android-sora-card"
include(":app")
include(":oauth")
