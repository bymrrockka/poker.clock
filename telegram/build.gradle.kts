import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "by.mrrockka"
version = "1.5.0-SNAPSHOT"

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

val jvmVersion = 22

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
    implementation(libs.bundles.logback)
    implementation(libs.bundles.springBoot)
    implementation(libs.bundles.db)
    implementation(libs.bundles.kotlinLibs)
    implementation(libs.bundles.exposed)
    //todo remove
    implementation(libs.mapstructCore)
    implementation(libs.lombokMapstructBinding)
    implementation(libs.apacheCommonsLang3)

    runtimeOnly(libs.aspectjWeaver)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstructProcessor)

    testImplementation(libs.bundles.test)
    testImplementation(libs.bundles.wiremock)
    testImplementation(libs.bundles.kotlinLibs)
    testImplementation(testFixtures(project))
    testImplementation(testFixtures(project(":core")))

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.mapstructProcessor)

    testFixturesApi(libs.springBootTelegrambots)
    testFixturesImplementation(project)
    testFixturesImplementation(testFixtures(project(":core")))
    testFixturesImplementation(libs.bundles.exposed)
    testFixturesImplementation(libs.wiremockContainer)
    testFixturesImplementation(libs.telegramBot)

    testFixturesCompileOnly(libs.lombok)
    testFixturesAnnotationProcessor(libs.lombok)
    testFixturesAnnotationProcessor(libs.mapstructProcessor)
}

configurations.all {
    resolutionStrategy.eachDependency {
        val serdeVer = "1.8.1"
        when (requested.module.toString()) {
            // json serialiazaton
            "org.jetbrains.kotlinx:kotlinx-serialization-json" -> useVersion(serdeVer)
            "org.jetbrains.kotlinx:kotlinx-serialization-json-jvm" -> useVersion(serdeVer)
            "org.jetbrains.kotlinx:kotlinx-serialization-core" -> useVersion(serdeVer)
            "org.jetbrains.kotlinx:kotlinx-serialization-core-jvm" -> useVersion(serdeVer)
            "org.jetbrains.kotlinx:kotlinx-serialization-bom" -> useVersion(serdeVer)
        }
    }
}

//todo: crop preview features after removing old repos with string block interpolation usage
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

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    withType<Test>().configureEach {
        jvmArgs("--enable-preview")
        useJUnitPlatform()
    }

    withType<JavaExec>().configureEach {
        jvmArgs("--enable-preview")
    }

    getByName<BootJar>("bootJar") {
        enabled = true
        archiveBaseName = "telegram-bot"
    }
}

application {
    mainClass = "by.mrrockka.TelegramApplication"
}

defaultTasks("clean", "assemble")