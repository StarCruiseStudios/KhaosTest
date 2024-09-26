/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

plugins {
    kotlin("jvm") version libs.versions.kotlin
}

description = """
    Project Name: ${rootProject.name}
""".trimIndent()

dependencies {
    testImplementation(project(":khaos-test-runner-junit5"))
    testImplementation(project(":khaos-test"))

    testImplementation(libs.jackson.dataformat.yaml)
    testImplementation(libs.jackson.databind)
    testImplementation(libs.kotlinLogging)
    testImplementation(libs.log4j.api)
    testImplementation(libs.log4j.core)
    testImplementation(libs.log4j.slf4j)
    testImplementation(kotlin("test"))
}

// =============================================================================
/*  Build Configuration */

tasks.named("test", Test::class.java) {
    useJUnitPlatform {
        includeEngines("khaos-test")
    }
    testLogging {
        events("passed", "skipped", "failed")
    }

    systemProperties(
        "com.starcruisestudios.khaos.test.failOnPending" to false
    )
}
