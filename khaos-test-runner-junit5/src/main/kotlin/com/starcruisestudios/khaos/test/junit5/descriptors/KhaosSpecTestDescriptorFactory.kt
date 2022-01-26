/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.FeatureDefinition
import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.junit5.util.childId
import org.junit.platform.engine.TestDescriptor
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

/**
 * Factory used to construct instances of the [KhaosSpecTestDescriptor] class.
 */
internal object KhaosSpecTestDescriptorFactory {
    private const val SPECIFICATION_SEGMENT_TYPE = "specification"

    /**
     * Uses the given [props] to construct a new [KhaosSpecTestDescriptor]
     * instance that is a child of the provided [parent].
     */
    fun build(props: KhaosSpecProps, parent: TestDescriptor): KhaosSpecTestDescriptor {
        val specificationTestId = parent.childId(SPECIFICATION_SEGMENT_TYPE, props.testClass.name)
        val specDescriptor = KhaosSpecTestDescriptor(
            props.testClass,
            props.specificationInstance.writer,
            props.testClass.name,
            specificationTestId)
        specDescriptor.setParent(parent)

        // Discover all properties that return the FeatureDefinition.
        props.specificationInstance::class.memberProperties
            .filter { it.returnType == FeatureDefinition::class.createType() }
            .forEach { feature ->
                val featureDefinition = feature.getter.call(props.specificationInstance) as FeatureDefinition
                val featureSteps = KhaosFeatureStepDefinition().apply(featureDefinition.buildFeature)
                val featureProps = KhaosFeatureProps(
                    feature.name,
                    featureDefinition.tags,
                    featureSteps,
                    specDescriptor)
                val featureDescriptor = KhaosFeatureTestDescriptorFactory.build(featureProps, specDescriptor)
                specDescriptor.addChild(featureDescriptor)
            }

        return specDescriptor
    }

    private fun getFeatureDefinition(
        feature: KProperty1<out KhaosSpecification, *>,
        props: KhaosSpecProps
    ): KhaosFeatureStepDefinition {
        val featureDefinition = feature.getter.call(props.specificationInstance) as FeatureDefinition
        return KhaosFeatureStepDefinition()
            .apply(featureDefinition.buildFeature)
    }
}
