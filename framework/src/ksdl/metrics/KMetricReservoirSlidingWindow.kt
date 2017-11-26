package ksdl.metrics

class KMetricReservoirSlidingWindow(size: Int = 1024) : KMetricReservoir {
    private val measurements: LongArray = LongArray(size)
    private var count: Long = 0

    override fun snapshot(): KMetricSnapshot = KMetricSnapshotUniform(measurements)

    override val size: Int get() = if (count > measurements.size.toLong()) measurements.size else count.toInt()

    override fun update(value: Long) {
        measurements[(count++ % measurements.size).toInt()] = value
    }
}