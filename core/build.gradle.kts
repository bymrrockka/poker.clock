import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

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
//    implementation(libs.bundles.logback)
    implementation(libs.bundles.db)
    implementation(libs.kotlinStdLib)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.jackson)
    //todo remove
    implementation(libs.mapstructCore)
    implementation(libs.lombokMapstructBinding)
    implementation(libs.hikariCP)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstructProcessor)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.mapstructProcessor)

    testImplementation(libs.bundles.test)
    testImplementation(libs.kotlinStdLib)

    testFixturesCompileOnly(libs.lombok)
    testFixturesAnnotationProcessor(libs.lombok)
    testFixturesAnnotationProcessor(libs.mapstructProcessor)

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

    getByName<BootJar>("bootJar") {
        enabled = false
    }

    getByName<BootRun>("bootRun") {
        enabled = false
    }

    getByName<Jar>("jar") {
        enabled = true
    }
}

defaultTasks("clean", "assemble")
