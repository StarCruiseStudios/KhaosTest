plugins {
    kotlin("jvm") version libs.versions.kotlin
}

allprojects {
    group = "com.starcruisestudios"
    version = "0.0.10"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}
