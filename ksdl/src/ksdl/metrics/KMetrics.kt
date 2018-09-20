package ksdl.metrics

import ksdl.system.*

@ThreadLocal
private val metricsValue = KMetrics() 

val KPlatform.metrics get() = metricsValue

// Metrics ksdl loosely modeled after dropwizard's
class KMetrics {
    private val reservoirs = mutableMapOf<String, KMetricReservoir>()

    fun reservoir(name: String): KMetricReservoir = reservoirs.getOrPut(name) { KMetricReservoirSlidingWindow() }

    fun register(name: String, reservoir: KMetricReservoir) = reservoirs.put(name, reservoir)
}
