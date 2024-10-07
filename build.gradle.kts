// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kover) apply false
    id("org.sonarqube") version "4.4.1.3373"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory.get().asFile.path)
}

val ktlint by configurations.creating

dependencies {
    ktlint("com.pinterest.ktlint:ktlint-cli:1.0.1") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
    // ktlint(project(":custom-ktlint-ruleset")) // in case of custom ruleset
}

tasks.register<JavaExec>("ktlint") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    // see https://pinterest.github.io/ktlint/install/cli/#command-line-usage for more information

    args(
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
        "--reporter=checkstyle,output=${project.layout.buildDirectory.file(
            "ktlintreport/ktlint.xml",
        ).get().asFile.path}",
    )
}

tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style and format"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    // see https://pinterest.github.io/ktlint/install/cli/#command-line-usage for more information
    args(
        "-F",
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}

sonarqube {
    properties {
        property("sonar.projectKey", "sora:sora-card-android")
        property("sonar.projectName", "sora-card-android")
        property("sonar.exclusions", "${project.projectDir}/**/*.txt")
        property(
            "sonar.junit.reportPaths",
            "${project.projectDir}/build/reports/tests/testReleaseUnitTest",
        )
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.rootDir}/report/coverage.xml")
    }
}
