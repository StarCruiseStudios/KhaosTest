import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

allprojects {
    group = "com.starcruisestudios"
    version = "0.1.0"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    // Shared configuration for every Kotlin module.
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
        }
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        dependencies {
            "implementation"(platform(rootProject.libs.junit.bom))
            "testImplementation"(platform(rootProject.libs.jackson.bom))
            "testImplementation"(platform(rootProject.libs.log4j.bom))
            "testImplementation"(rootProject.libs.bundles.test.logging)
            "testImplementation"(kotlin("test"))
            "testRuntimeOnly"(rootProject.libs.junit.platform.launcher)
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }

        tasks.withType<Jar>().configureEach {
            manifest {
                attributes(
                    "Specification-Title" to project.name,
                    "Specification-Version" to project.version,
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version
                )
            }
        }
    }

    // Shared configuration for every published module.
    pluginManager.withPlugin("maven-publish") {
        extensions.configure<PublishingExtension> {
            publications {
                create<MavenPublication>("maven") {
                    from(components["kotlin"])
                }
            }
        }
    }
}
