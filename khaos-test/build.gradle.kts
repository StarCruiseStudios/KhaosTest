plugins {
    kotlin("jvm") version libs.versions.kotlin

    alias(libs.plugins.gradle.maven.publish)
    alias(libs.plugins.jetbrains.kover)
}

description = """
    Khaos Test is a Kotlin test framework and toolkit for improving self-documentation, readability, and debugging of tests.
""".trimIndent()

dependencies {
    implementation(libs.apache.commons.lang3)
    implementation(libs.kotlinLogging)
    implementation(libs.junit.platform.commons)
    implementation(libs.slf4j.api)

    testImplementation(libs.jackson.dataformat.yaml)
    testImplementation(libs.jackson.databind)
    testImplementation(libs.log4j.api)
    testImplementation(libs.log4j.core)
    testImplementation(libs.log4j.slf4j)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
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
