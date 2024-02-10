package dev.jimmymorales.featureflags.core

import co.touchlab.stately.collections.ConcurrentMutableList

/**
 * Check whether a feature should be enabled or not. Based on the priority of the different
 * providers and if said provider explicitly defines a value for that feature, the value of the flag
 * is returned.
 */
object RuntimeBehavior {
    private val providers = ConcurrentMutableList<FeatureFlagProvider>()

    fun isFeatureEnabled(feature: Feature): Boolean {
        return providers.filter { it.hasFeature(feature) }
            .minByOrNull { it.priority.value }
            ?.isFeatureEnabled(feature)
            ?: feature.defaultValue
    }

    fun addProvider(provider: FeatureFlagProvider) {
        providers.add(provider)
    }

    fun clearFeatureFlagProviders() {
        providers.clear()
    }
}
