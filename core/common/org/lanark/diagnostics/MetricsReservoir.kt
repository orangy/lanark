package org.lanark.diagnostics

interface MetricsReservoir {
    val size: Int
    fun update(value: Long)

    fun snapshot(): MetricSnapshot
}

