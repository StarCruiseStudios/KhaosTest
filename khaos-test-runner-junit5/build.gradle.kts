/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

description = """
    Project Name: ${rootProject.name}
""".trimIndent()

dependencies {
    implementation(project(":khaos-test"))
    implementation(kotlin("reflect"))
    implementation(libs.kotlin.logging)
    implementation(libs.junit.platform.engine)
    implementation(libs.junit.platform.commons)
    implementation(libs.slf4j.api)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit.platform.testkit)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}
