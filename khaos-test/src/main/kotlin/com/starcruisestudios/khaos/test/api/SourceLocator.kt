/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiPredicate

/**
 * Represents a source file location used for IDE navigation.
 */
data class SourceLocation(val filePath: String, val lineNumber: Int)

/**
 * Locates the call site for DSL declarations to support IDE test mapping.
 */
object SourceLocator {
    private const val MAX_SEARCH_DEPTH = 25
    private val pathCache = ConcurrentHashMap<String, String?>()

    fun capture(): SourceLocation? {
        val element = Throwable().stackTrace.firstOrNull(::isUserFrame) ?: return null
        val fileName = element.fileName ?: return null
        val lineNumber = element.lineNumber
        if (lineNumber <= 0) return null

        val filePath = pathCache.computeIfAbsent(fileName, ::findFilePath) ?: return null
        return SourceLocation(filePath, lineNumber)
    }

    private fun isUserFrame(element: StackTraceElement): Boolean {
        val className = element.className
        return element.fileName != null &&
            element.lineNumber > 0 &&
            !className.startsWith("com.starcruisestudios.khaos.test") &&
            !className.startsWith("kotlin.") &&
            !className.startsWith("java.") &&
            !className.startsWith("org.junit.")
    }

    private fun findFilePath(fileName: String): String? {
        val root = Paths.get(System.getProperty("user.dir"))
        if (!Files.exists(root)) return null

        val matcher = root.fileSystem.getPathMatcher("glob:**/$fileName")
        val filter = BiPredicate<Path, BasicFileAttributes> { path, attrs ->
            attrs.isRegularFile &&
                matcher.matches(path) &&
                path.isLikelySourceFile()
        }

        Files.find(root, MAX_SEARCH_DEPTH, filter).use { stream ->
            val match = stream.findFirst()
            return if (match.isPresent) match.get().toAbsolutePath().toString() else null
        }
    }

    private fun Path.isLikelySourceFile(): Boolean {
        val normalized = this.toString()
        val sep = File.separator
        return normalized.contains("${sep}src${sep}") &&
            !normalized.contains("${sep}build${sep}") &&
            !normalized.contains("${sep}out${sep}") &&
            !normalized.contains("${sep}.gradle${sep}")
    }
}
