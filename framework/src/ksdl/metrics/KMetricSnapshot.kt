package ksdl.metrics

interface KMetricSnapshot {
    val values: LongArray
    val size: Int
    fun min(): Long
    fun max(): Long
    fun mean(): Double

    fun valueAt(quantile: Double): Double
    fun standardDeviation(): Double
}

fun KMetricSnapshot.median() = valueAt(0.5)
