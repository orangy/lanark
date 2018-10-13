package org.lanark.system

import kotlin.math.*

fun round(value: Double, decimals: Int): Double {
    val multiplier = 10.0.pow(decimals)
    return kotlin.math.round(value * multiplier) / multiplier
}
