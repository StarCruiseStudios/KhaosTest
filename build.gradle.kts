plugins {
    kotlin("jvm") version libs.versions.kotlin
}

allprojects {
    group = "com.starcruisestudios"
    version = "0.1.0"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}
