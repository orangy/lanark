package ksdl.metrics

import ksdl.system.*

val KPlatform.metrics get() = metricsValue

private val metricsValue = KMetrics()

// Metrics ksdl loosely modeled after dropwizard's
class KMetrics {
    private val reservoirs = mutableMapOf<String, KMetricReservoir>()

    fun reservoir(name: String): KMetricReservoir = reservoirs.getOrPut(name) { KMetricReservoirSlidingWindow() }

    fun register(name: String, reservoir: KMetricReservoir) = reservoirs.put(name, reservoir)
}
