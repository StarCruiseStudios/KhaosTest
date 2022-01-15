/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

pluginManagement {
    repositories {
        // Default gradle plugin portal.
        gradlePluginPortal()

        // Pulls development dependencies from local machine.
        mavenLocal()
    }
}

rootProject.name = "khaos-test"
include(
    ":khaos-test",
    ":khaos-test-runner-junit5",
    ":khaos-test-example"
)