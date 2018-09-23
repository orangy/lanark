package org.lanark.diagnostics

// Metrics loosely modeled after dropwizard's
class Metrics {
    private val reservoirs = mutableMapOf<String, MetricsReservoir>()

    fun reservoir(name: String): MetricsReservoir = reservoirs.getOrPut(name) { MetricsReservoirSlidingWindow() }

    fun register(name: String, reservoir: MetricsReservoir) = reservoirs.put(name, reservoir)
}
