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

    // Only the framework's own packages are skipped, so user code that lives
    // under com.starcruisestudios.khaos.test (such as the examples) is found.
    private val frameworkPackages = listOf(
        "com.starcruisestudios.khaos.test.api.",
        "com.starcruisestudios.khaos.test.exceptions.",
        "com.starcruisestudios.khaos.test.validate.",
        "com.starcruisestudios.khaos.test.verification.",
        "com.starcruisestudios.khaos.test.junit5."
    )
    private val walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

    fun capture(): SourceLocation? {
        val frame = walker.walk { frames -> frames.filter(::isUserFrame).findFirst().orElse(null) } ?: return null
        val fileName = frame.fileName ?: return null
        val lineNumber = frame.lineNumber
        if (lineNumber <= 0) return null

        // Include the package so same-named files in different packages are told apart.
        val relativePath = frame.declaringClass.packageName.replace('.', '/') + "/" + fileName
        val filePath = pathCache.computeIfAbsent(relativePath, ::findFilePath) ?: return null
        return SourceLocation(filePath, lineNumber)
    }

    private fun isUserFrame(element: StackWalker.StackFrame): Boolean {
        val className = element.className
        return element.fileName != null &&
            element.lineNumber > 0 &&
            frameworkPackages.none { className.startsWith(it) } &&
            !className.startsWith("kotlin.") &&
            !className.startsWith("java.") &&
            !className.startsWith("org.junit.")
    }

    private fun findFilePath(relativePath: String): String? {
        val root = Paths.get(System.getProperty("user.dir"))
        if (!Files.exists(root)) return null

        val matcher = root.fileSystem.getPathMatcher("glob:**/$relativePath")
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
