pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "khaos-test"
include(
    ":khaos-test",
    ":khaos-test-runner-junit5",
    ":khaos-test-example"
)