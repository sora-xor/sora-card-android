plugins {
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.kapt") version "1.8.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
    id("com.google.dagger.hilt.android") version "2.45" apply false
    id("org.sonarqube") version "4.3.1.3277"
    id("jacoco")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

fun Project.getKtlintConfiguration(): Configuration {
    return configurations.findByName("ktlint") ?: configurations.create("ktlint") {
        val dependency = project.dependencies.create("com.pinterest:ktlint:0.46.1").apply {
            attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
        dependencies.add(dependency)
    }
}

tasks.register("ktlint", JavaExec::class) {
    description = "Check Kotlin code style."
    group = "Verification"
    classpath = getKtlintConfiguration()
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf(
        "--reporter=plain",
        "--android",
        "--reporter=checkstyle,output=${project.buildDir}/reports/checkstyle/ktlint.xml",
        "$project.rootDir/**/src/main/**/*.kt"
    )
}

tasks.register("ktlintFormat", JavaExec::class) {
    description = "Fix Kotlin code style deviations."
    group = "formatting"
    classpath = getKtlintConfiguration()
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("-F", "$project.rootDir/**/src/main/**/*.kt", "--android")
}

subprojects {
    apply plugin: 'org.sonarqube'
    apply plugin: 'jacoco'

    task testCoverage(type: JacocoReport) {
        reports {
            xml.enabled true
            html.enabled true
        }

        def excludes = [
                '**/R.class',
                '**/R$*.class',
                '**/BuildConfig.*',
                '**/Manifest*.*',
                '**/*Test*.*',
                'android/**/*.*',
                'androidx/**/*.*',
                '**/*$ViewInjector*.*',
                '**/*Dagger*.*',
                '**/*MembersInjector*.*',
                '**/*_Factory.*',
                '**/*_Provide*Factory*.*',
                '**/*_ViewBinding*.*',
                '**/AutoValue_*.*',
                '**/R2.class',
                '**/R2$*.class',
                '**/*Directions$*',
                '**/*Directions.*',
                '**/*Binding.*'
        ]

    }

    test {
        useJUnitPlatform()
        failFast = true
        testLogging {
            events "passed", "skipped", "failed"
        }
        reports {
            junitXml.enabled = true
        }
        finalizedBy testCoverage
    }

    jacoco {
        toolVersion = "0.8.8"
        reportsDir = file("$buildDir/reports/")
    }

    task printSonarProps {
        println "${project.group.split('\\.')[-1]}:${rootProject.name}.${project.name}"
    }
}