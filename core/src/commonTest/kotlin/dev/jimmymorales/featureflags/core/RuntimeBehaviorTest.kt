package dev.jimmymorales.featureflags.core

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class RuntimeBehaviorTest {
    private val featureDefaultOff = FeatureFlag.JETPACK_COMPOSE
    private val featureDefaultOn = FeatureFlag.STRICT_MODE

    @BeforeTest
    fun setUp() {
        RuntimeBehavior.clearFeatureFlagProviders()
    }

    @Test
    fun `should return default value when no provider`() {
        assertEquals(RuntimeBehavior.isFeatureEnabled(featureDefaultOff), featureDefaultOff.defaultValue)
    }

    @Test
    fun `should get value from provider when added`() {
        RuntimeBehavior.addProvider(TestProvider())

        assertTrue { RuntimeBehavior.isFeatureEnabled(featureDefaultOff) }
    }

    @Test
    fun `should get default value when provider doesn't have value`() {
        RuntimeBehavior.addProvider(TestProvider())

        assertEquals(RuntimeBehavior.isFeatureEnabled(featureDefaultOn), featureDefaultOn.defaultValue)
    }

    @Test
    fun `should get default value from highest priority provider`() {
        RuntimeBehavior.addProvider(TestProvider())
        RuntimeBehavior.addProvider(MaxPriorityTestProvider())

        assertFalse { RuntimeBehavior.isFeatureEnabled(featureDefaultOff) }
    }

    enum class FeatureFlag(
        override val key: String,
        override val title: String,
        override val explanation: String,
        override val defaultValue: Boolean = true
    ) : Feature {
        JETPACK_COMPOSE(
            "feature.jetpackcompose",
            "Jetpack Compose",
            "Enabled Jetpack Compose UI",
            defaultValue = false
        ),
        STRICT_MODE(
            "testsetting.strictmode",
            "Enable strict mode",
            "Detect IO operations accidentally performed on the main Thread",
            defaultValue = true
        )
    }

    inner class TestProvider : FeatureFlagProvider {
        override val priority = FeaturePriority.Min

        override fun isFeatureEnabled(feature: Feature): Boolean = true

        override fun hasFeature(feature: Feature): Boolean = feature == featureDefaultOff
    }

    inner class MaxPriorityTestProvider : FeatureFlagProvider {
        override val priority = FeaturePriority.Max

        override fun isFeatureEnabled(feature: Feature): Boolean = false

        override fun hasFeature(feature: Feature): Boolean = true
    }
}