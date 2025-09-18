import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "by.mrrockka"
version = "1.5.0-SNAPSHOT"

plugins {
    java
    idea
    `maven-publish`
    `java-test-fixtures`
    alias(libs.plugins.springBootPlugin)
    alias(libs.plugins.springDepManagementPlugin)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerializationPlugin)
}

val jvmVersion = 21

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(jvmVersion)
    }
}

kotlin {
    jvmToolchain(jvmVersion)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.bundles.db)
    implementation(libs.kotlinStdLib)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.jackson)
    implementation(libs.hikariCP)

    testImplementation(libs.bundles.test)
    testImplementation(libs.kotlinStdLib)

    testFixturesImplementation(libs.bundles.exposed)
    testFixturesApi(libs.bundles.test)
    testFixturesApi(libs.springBootJdbc)
    testFixturesApi(libs.bundles.testContainer)
    testFixturesApi(libs.javaFaker) {
        exclude("org.yaml")
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(
                listOf(
                        "--enable-preview",
                        "-Amapstruct.suppressGeneratorTimestamp=true",
                        "-Amapstruct.defaultComponentModel=spring",
                ),
        )
    }

    withType<Test>().configureEach {
        jvmArgs("--enable-preview")
        useJUnitPlatform()
    }

    withType<JavaExec>().configureEach {
        jvmArgs("--enable-preview")
    }

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    bootJar {
        enabled = true
        archiveBaseName = "telegram-bot"
    }

    bootRun {
        enabled = false
    }

    jar {
        enabled = true
    }
}

defaultTasks("clean", "assemble")
