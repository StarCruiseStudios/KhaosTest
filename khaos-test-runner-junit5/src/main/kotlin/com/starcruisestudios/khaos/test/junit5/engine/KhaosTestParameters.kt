/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import org.junit.platform.engine.ConfigurationParameters

/**
 * Gets a [KhaosTestParameters] instance that provides access to Khaos Test
 * config values.
 */
fun ConfigurationParameters.khaosParameters() : KhaosTestParameters {
    return KhaosTestParameters(this)
}

/**
 * Provides access to Khaos Test configuration parameters.
 *
 * @param config The [ConfigurationParameters] instance used to retrieve
 *   configuration values.
 */
class KhaosTestParameters(private val config: ConfigurationParameters) {

    /**
     * Represents a tag filter used to include or exclude tests based on tags.
     *
     * @param tag The tag to filter on.
     * @param include True to include tests with the tag, false to exclude them.
     */
    data class TagFilter(val tag: String, val include: Boolean)
    
    companion object {
        private const val FAIL_ON_PENDING = "com.starcruisestudios.khaos.test.failOnPending"
        private const val PARALLEL = "junit.jupiter.execution.parallel.enabled"
        private const val TAGS = "junit.jupiter.tags"
    }

    /**
     * Indicates whether test scenarios should be considered failed if a pending
     * result is returned.
     *
     * Specify using the "com.starcruisestudios.khaos.test.failOnPending"
     * property. Defaults to true.
     */
    val failOnPending: Boolean get() {
        return config.getBoolean(FAIL_ON_PENDING).orElse(true)
    }

    /**
     * Indicates whether test scenarios should be executed in parallel.
     *
     * Specify using the "junit.jupiter.execution.parallel.enabled" property.
     * Defaults to true.
     */
    val parallel: Boolean get() {
        return config.getBoolean(PARALLEL).orElse(true)
    }
    
    /**
     * The set of tag filters to apply when executing tests.
     *
     * Specify using the "junit.jupiter.tags" property. Tags should be
     * comma-separated. Prefix a tag with "-" to exclude it.
     *
     * Examples:
     *   "fast, database" - includes tests with either the "fast" or
     *     "database" tags.
     *   "fast, -database" - includes tests with the "fast" tag, but excludes
     *     those with the "database" tag.
     */
    val tags: Set<TagFilter> get() {
        return config.get(TAGS).map {
            it.split(',')
                .map { tag -> tag.trim() }
                .filter { tag -> tag.isNotEmpty() }
                .map { tag -> TagFilter(tag.removePrefix("-"), !tag.startsWith('-'))}
                .toSet()
        }.orElse(emptySet())
    }
}
