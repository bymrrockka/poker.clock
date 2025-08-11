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
    implementation(libs.springBootJdbc)
    implementation(libs.springBootWeb)
    implementation(libs.springBootAop)
    implementation(libs.springBootTelegrambots)
    implementation(project(":core"))
    implementation(libs.apacheCommonsLang3)
    implementation(libs.postgresql)
    implementation(libs.liquibase)
    implementation(libs.mapstructCore)
    implementation(libs.lombokMapstructBinding)
    implementation(libs.kotlinStdLib)
    implementation(libs.exposedCore)
    implementation(libs.exposedJdbc)
    implementation(libs.exposedJson)
    implementation(libs.exposedJavaTime)
    implementation(libs.exposedSpringBoot)
    implementation(libs.telegramBot)
    implementation(libs.vendeliKsp)
    implementation(libs.vendeliSpringStarter)

    runtimeOnly(libs.aspectjWeaver)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstructProcessor)

    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoJupiter)
    testImplementation(libs.jupiter)
    testImplementation(libs.assertjCore)
    testImplementation(libs.kotlinStdLib)
    testImplementation(libs.mockk)
    testImplementation(libs.springBootTest)
    testImplementation(libs.awaitility)
    testImplementation(libs.wiremock)
    testImplementation(libs.wiremockKotlinDsl)
    testImplementation(libs.wiremockContainer)
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
    testFixturesImplementation(libs.vendeliKsp)

    testFixturesCompileOnly(libs.lombok)
    testFixturesAnnotationProcessor(libs.lombok)
    testFixturesAnnotationProcessor(libs.mapstructProcessor)
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(
            listOf(
                    "--enable-preview",
                    "-Amapstruct.suppressGeneratorTimestamp=true",
                    "-Amapstruct.defaultComponentModel=spring",
            ),
    )
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    archiveBaseName = "telegram-bot"
}


application {
    mainClass = "by.mrrockka.TelegramApplication"
}

defaultTasks("clean", "assemble")



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