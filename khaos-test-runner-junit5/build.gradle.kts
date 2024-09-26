/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

plugins {
    kotlin("jvm") version libs.versions.kotlin

    id("org.gradle.maven-publish")
    alias(libs.plugins.jetbrains.kover)
}

description = """
    Project Name: ${rootProject.name}
""".trimIndent()

dependencies {
    implementation(project(":khaos-test"))
    implementation(kotlin("reflect"))
    implementation(libs.kotlinLogging)
    implementation(libs.junit.jupiter.engine)
    implementation(libs.slf4j.api)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.jackson.dataformat.yaml)
    testImplementation(libs.jackson.databind)
    testImplementation(libs.log4j.api)
    testImplementation(libs.log4j.core)
    testImplementation(libs.log4j.slf4j)
    testImplementation(libs.junit.platform.testkit)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test"))

}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}

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
