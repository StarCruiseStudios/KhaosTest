plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

description = """
    Khaos Test is a Kotlin test framework and toolkit for improving self-documentation, readability, and debugging of tests.
""".trimIndent()

dependencies {
    implementation(libs.kotlin.logging)
    implementation(libs.junit.platform.commons)
    implementation(libs.slf4j.api)
    testImplementation(libs.khaos.lang)
}
