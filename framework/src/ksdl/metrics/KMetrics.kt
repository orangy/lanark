package ksdl.metrics

// Metrics framework loosely modeled after dropwizard's
object KMetrics {
    private val reservoirs = mutableMapOf<String, KMetricReservoir>()

    fun reservoir(name: String): KMetricReservoir = reservoirs.getOrPut(name) { KMetricReservoirSlidingWindow() }

    fun register(name: String, reservoir: KMetricReservoir) = reservoirs.put(name, reservoir)
}




