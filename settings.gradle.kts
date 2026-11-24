pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

// The version catalog is loaded automatically from gradle/libs.versions.toml.

rootProject.name = "khaos-test-build"
include(
    ":khaos-test",
    ":khaos-test-runner-junit5",
    ":khaos-test-example"
)
if (gradle.parent == null) { includeBuild("../Khaos") }
