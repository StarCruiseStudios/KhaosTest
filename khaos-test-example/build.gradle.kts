/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

plugins {
    alias(libs.plugins.kotlin.jvm)
}

description = """
    Project Name: ${rootProject.name}
""".trimIndent()

dependencies {
    testImplementation(project(":khaos-test-runner-junit5"))
    testImplementation(project(":khaos-test"))

    testImplementation(libs.khaos.lang)
    testImplementation(libs.kotlin.logging)
}

tasks.test {
    useJUnitPlatform {
        includeEngines("khaos-test")
    }

    systemProperties(
        "com.starcruisestudios.khaos.test.failOnPending" to false
    )
}
