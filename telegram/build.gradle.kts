import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "by.mrrockka"
version = "1.5.1"

plugins {
    java
    idea
    application
    `maven-publish`
    `java-test-fixtures`
    alias(libs.plugins.springBootPlugin)
    alias(libs.plugins.springDepManagementPlugin)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.googleDevtoolsKspPlugin)
    alias(libs.plugins.telegramBot)
}

val jvmVersion = 21

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(jvmVersion)
    }
}

kotlin {
    jvmToolchain(jvmVersion)
    sourceSets.test {
        kotlin.srcDir("src/main/java/")
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.logging)
    implementation(libs.bundles.springBoot)
    implementation(libs.bundles.db)
    implementation(libs.bundles.kotlinLibs)
    implementation(libs.bundles.exposed)

    runtimeOnly(libs.aspectjWeaver)

    testImplementation(libs.bundles.test)
    testImplementation(libs.bundles.kotlinLibs)
    testImplementation(testFixtures(project))
    testImplementation(testFixtures(project(":core")))

    testFixturesImplementation(project)
    testFixturesImplementation(testFixtures(project(":core")))
    testFixturesImplementation(libs.bundles.exposed)
    testFixturesImplementation(libs.telegramBot)
}

configurations.all {
    resolutionStrategy.eachDependency {
        val serdeVer = "1.8.1"
        when (requested.module.toString()) {
            // json serialization
            "org.jetbrains.kotlinx:kotlinx-serialization-json" -> useVersion(serdeVer)
            "org.jetbrains.kotlinx:kotlinx-serialization-json-jvm" -> useVersion(serdeVer)
            "org.jetbrains.kotlinx:kotlinx-serialization-core" -> useVersion(serdeVer)
            "org.jetbrains.kotlinx:kotlinx-serialization-core-jvm" -> useVersion(serdeVer)
            "org.jetbrains.kotlinx:kotlinx-serialization-bom" -> useVersion(serdeVer)
        }
    }
}

tasks {
    withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    bootJar {
        enabled = true
        archiveBaseName = "telegram-bot"
    }
}

defaultTasks("clean")