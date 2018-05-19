package ksdl.system

import platform.posix.*
import sdl2.*

object KMath {
    val pi: Double = 3.1415926535897932
    fun abs(value: Double) = if (value < 0) -value else value
    fun pow(value: Int, power: Int): Int {
        var result = value
        for (i in 2..power)
            result *= value
        return result
    }

    fun round(value: Double, decimals: Int): Double {
        val multiplier = pow(10, decimals)
        return round(value * multiplier) / multiplier
    }
}