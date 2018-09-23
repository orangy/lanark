package org.lanark.diagnostics

class MetricsReservoirSlidingWindow(size: Int = 1024) : MetricsReservoir {
    private val measurements: LongArray = LongArray(size)
    private var count: Long = 0

    override fun snapshot(): MetricSnapshot = MetricSnapshotUniform(measurements)

    override val size: Int get() = if (count > measurements.size.toLong()) measurements.size else count.toInt()

    override fun update(value: Long) {
        measurements[(count++ % measurements.size).toInt()] = value
    }
}