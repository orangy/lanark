package ksdl.metrics

interface KMetricReservoir {
    val size: Int
    fun update(value: Long)

    fun snapshot(): KMetricSnapshot
}

