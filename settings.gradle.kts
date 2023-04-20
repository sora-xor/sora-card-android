import java.util.Properties
import java.io.FileInputStream

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

fun secret(name: String): String? {
    val pr = runCatching { FileInputStream("local.properties") }.getOrNull()?.let { file ->
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

        // Have to add jcenter repo because of KYC sdk dependencies
        jcenter()

        maven {
            url = uri(secret("PAY_WINGS_REPOSITORY_URL")!!)
            credentials {
                username = secret("PAY_WINGS_USERNAME")
                password = secret("PAY_WINGS_PASSWORD")
            }
        }

        mavenLocal()
        maven {
            url = uri(secret("IROHA_NEXUS")!!)
        }
    }
}

rootProject.name = "android-sora-card"
include(":app")
include(":oauth")
