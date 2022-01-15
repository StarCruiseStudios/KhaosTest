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
     * https://detekt.github.io/detekt/gradle.html
     */
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
}

repositories {
    mavenCentral()
}

// =============================================================================
/*  Project Properties */

group = "com.starcruisestudios"
version = "1.0.0"
description = """
    Project Name: ${rootProject.name}
""".trimIndent()

// =============================================================================
/*  Dependencies */

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(project(":khaos-test-runner-junit5"))
    testImplementation(project(":khaos-test"))
    
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    testImplementation("io.github.microutils:kotlin-logging:2.1.21")
    testImplementation("org.apache.logging.log4j:log4j-api:2.17.0")
    testImplementation("org.apache.logging.log4j:log4j-core:2.17.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.platform:junit-platform-console:1.8.2")
    testImplementation("org.slf4j:slf4j-api:1.7.32")
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
    useJUnitPlatform {
        includeEngines("junit-jupiter", "khaos-test")
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}

detekt {
    config = files("../config/detekt/detekt.yml")
}
