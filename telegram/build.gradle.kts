import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "by.mrrockka"
version = "1.4.3-SNAPSHOT"

plugins {
    java
    idea
    application
    `maven-publish`
    `java-test-fixtures`
    `jvm-test-suite`
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
    implementation(libs.vendeliSpringStarter)
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
    testFixturesImplementation(libs.wiremockContainer)
    testFixturesImplementation(libs.telegramBot)

    testFixturesCompileOnly(libs.lombok)
    testFixturesAnnotationProcessor(libs.lombok)
    testFixturesAnnotationProcessor(libs.mapstructProcessor)
}

//todo: crop preview features after removing old repos with string block interpolation usage

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


//todo: remove after scenario tests complited
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
        register<JvmTestSuite>("integrationTest") {

            targets {
                all {
                    testTask.configure {
                        jvmArgs("--enable-preview")
                        testLogging {
                            events("passed", "failed")

                        }
                    }
                }
            }

            dependencies {
                implementation(libs.springBootJdbc)
                implementation(libs.springBootWeb)
                implementation(libs.springBootAop)
                implementation(libs.springBootTelegrambots)
                implementation(libs.exposedCore)
                implementation(libs.exposedJdbc)
                implementation(libs.exposedJson)
                implementation(libs.exposedJavaTime)
                implementation(libs.exposedSpringBoot)
                implementation(libs.assertjCore)
                implementation(libs.springBootTest)
                implementation(libs.liquibase)
                runtimeOnly(libs.aspectjWeaver)
                implementation(libs.kotlinStdLib)
                implementation(libs.mockk)
                implementation(libs.springMockk)
                implementation(libs.awaitility)

                compileOnly(libs.lombok)
                annotationProcessor(libs.lombok)
                annotationProcessor(libs.mapstructProcessor)

                implementation(project())
                implementation(testFixtures(project()))
                implementation(testFixtures(project(":core")))
            }
        }
    }
}