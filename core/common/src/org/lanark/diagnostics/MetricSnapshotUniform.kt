package org.lanark.diagnostics

import kotlin.math.*

class MetricSnapshotUniform(values: LongArray) : MetricSnapshot {
    override val values: LongArray = values.sortedArray()
    override val size: Int get() = values.size

    constructor(values: Iterable<Long>) : this(values.toList().toLongArray())

    override fun valueAt(quantile: Double): Double {
        if (quantile < 0.0 || quantile > 1.0 || quantile.isNaN())
            throw IllegalArgumentException("$quantile is not in [0..1]")

        if (size == 0) return 0.0

        val pos = quantile * (values.size + 1)
        val index = pos.toInt()
        return when {
            index < 1 -> values[0].toDouble()
            index >= values.size -> values[values.size - 1].toDouble()
            else -> {
                val lower = values[index - 1]
                val upper = values[index]
                lower + (pos - floor(pos)) * (upper - lower)
            }
        }
    }

    override fun min(): Long = when (size) {
        0 -> 0
        else -> values[0]
    }

    override fun max(): Long = when (size) {
        0 -> 0
        else -> values[values.lastIndex]
    }

    override fun mean(): Double = values.sumByDouble { it.toDouble() } / size

    override fun standardDeviation(): Double {
        // two-pass algorithm for variance, avoids numeric overflow
        if (size <= 1)
            return 0.0

        val mean = mean()
        val sum = values.sumByDouble { (it - mean).let { it * it } }
        val variance = sum / values.lastIndex
        return sqrt(variance)
    }
}

