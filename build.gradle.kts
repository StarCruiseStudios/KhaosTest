/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

// =============================================================================
/*  Build Configuration */

plugins {
    /**
     * Provides Gradle tasks and configuration for building Kotlin projects for
     * the JVM.
     *
     * https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
     */
    kotlin("jvm").version("1.6.10")

    /**
     * Generates API Documentation.
     *
     * https://github.com/Kotlin/dokka
     */
    id("org.jetbrains.dokka") version "1.6.10"
}

repositories {
    mavenCentral()
}

val wrapper: Wrapper by tasks
wrapper.gradleVersion = "7.3.2"
