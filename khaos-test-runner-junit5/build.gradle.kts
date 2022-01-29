/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// =============================================================================
/*  Build Configuration */

plugins {
    /**
     * The built in Gradle Base plugin. This provides tasks and conventions that
     * are common to most builds.
     * https://docs.gradle.org/current/userguide/base_plugin.html
     */
    base

    /**
     * Provides Gradle tasks and configuration for building Kotlin projects for
     * the JVM.
     * https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
     */
    kotlin("jvm")

    /**
     * Provides the ability to publish build artifacts to a Maven repository.
     *
     * https://docs.gradle.org/current/userguide/publishing_maven.html
     */
    id("org.gradle.maven-publish")

    /**
     * Generates API Documentation.
     *
     * https://github.com/Kotlin/dokka
     */
    id("org.jetbrains.dokka")

    /**
     * https://detekt.github.io/detekt/gradle.html
     */
    id("io.gitlab.arturbosch.detekt") version "1.19.0"

    /**
     * Kotlin Code Coverage plugin.
     *
     * https://github.com/Kotlin/kotlinx-kover
     */
    id("org.jetbrains.kotlinx.kover") version "0.4.4"
}

repositories {
    mavenCentral()
}

// =============================================================================
/*  Project Properties */

group = "com.starcruisestudios"
version = "0.0.4"
description = """
    Project Name: ${rootProject.name}
""".trimIndent()

// =============================================================================
/*  Dependencies */

dependencies {
    implementation(project(":khaos-test"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("io.github.microutils:kotlin-logging:2.1.21")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    implementation("org.slf4j:slf4j-api:1.7.33")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    testImplementation("org.apache.logging.log4j:log4j-api:2.17.0")
    testImplementation("org.apache.logging.log4j:log4j-core:2.17.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.junit.platform:junit-platform-console:1.8.2")
    testImplementation("org.junit.platform:junit-platform-testkit:1.8.2")
    testImplementation("io.mockk:mockk:1.12.2")

}

// =============================================================================
/*  Kotlin Compilation Configuration */

val javaCompatibilityVersion = JavaVersion.VERSION_1_8
val kotlinVersion = "1.6"
tasks.withType(KotlinCompile::class.java) {
    kotlinOptions {
        jvmTarget = javaCompatibilityVersion.toString()
        languageVersion = kotlinVersion
        apiVersion = kotlinVersion
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
    sourceCompatibility = javaCompatibilityVersion.toString()
    targetCompatibility = javaCompatibilityVersion.toString()
}

java {
    sourceCompatibility = javaCompatibilityVersion
    targetCompatibility = javaCompatibilityVersion
}


// =============================================================================
/*  Build Configuration */

tasks.named("test", Test::class.java) {
    useJUnitPlatform {useJUnitPlatform {
        includeEngines("junit-jupiter")
    }}
    testLogging {
        events("passed", "skipped", "failed")
    }
}

detekt {
    config = files("../config/detekt/detekt.yml")
}

// =============================================================================
/*  Publish Configuration */

tasks.withType(Jar::class.java) {
    doFirst {
        manifest {
            attributes(mapOf(
                "Specification-Title" to project.name,
                "Specification-Version" to project.version,
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            ))
        }
    }
}

publishing {
    repositories {
    }
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("kotlin"))
        }
    }
}
