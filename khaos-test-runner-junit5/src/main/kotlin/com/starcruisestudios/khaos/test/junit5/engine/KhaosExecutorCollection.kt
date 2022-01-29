/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosLogContext
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor

/**
 * A collection of [KhaosExecutor]s that is used to execute tests with arbitrary
 * [TestDescriptor]s.
 */
internal class KhaosExecutorCollection private constructor(
    private val executors: Map<Class<*>, KhaosExecutor<*>>
){
    /**
     * Executes the test described by the given [request] and [testDescriptor]
     * using the given [logContext] to support nested execution.
     */
    suspend fun <T : TestDescriptor> execute(
        request: ExecutionRequest,
        testDescriptor: T,
        logContext: KhaosLogContext
    ) {
        @Suppress("UNCHECKED_CAST")
        val executor = executors[testDescriptor.javaClass] as? KhaosExecutor<T>
        checkNotNull(executor) {
            "Could not find an executor to handle tests for type ${testDescriptor.javaClass}."
        }

        executor.executeDescriptor(request, testDescriptor, this, logContext)
    }

    companion object {
        /**
         * Builder function used to construct new [KhaosExecutorCollection]
         * instances.
         */
        fun build(block: Builder.() -> Unit): KhaosExecutorCollection {
            return BuilderImpl().apply(block).build()
        }
    }

    /**
     * Builder class used to construct new [KhaosExecutorCollection] instances.
     */
    sealed class Builder {
        protected val executors = mutableMapOf<Class<*>, KhaosExecutor<*>>()

        /**
         * Provides a [KhaosExecutor] that will be added to the collection of
         * executors.
         *
         * @throws IllegalStateException if an executor that handles the given
         *   [TestDescriptor] type [T] is already defined.
         */
        inline fun <reified T : TestDescriptor> withExecutor(executor: KhaosExecutor<T>) {
            check(!executors.containsKey(T::class.java)) {
                "An executor that handles test descriptors of type ${T::class.java} is already defined."
            }

            executors[T::class.java] = executor
        }
    }

    private class BuilderImpl : Builder(){
        fun build() : KhaosExecutorCollection {
            return KhaosExecutorCollection(executors)
        }
    }
}
