package org.lanark.diagnostics

interface MetricSnapshot {
    val values: LongArray
    val size: Int
    fun min(): Long
    fun max(): Long
    fun mean(): Double

    fun valueAt(quantile: Double): Double
    fun standardDeviation(): Double
}

fun MetricSnapshot.median() = valueAt(0.5)
