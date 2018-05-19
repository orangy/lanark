package ksdl.geometry

import platform.posix.*

data class KVector(val x: Int, val y: Int) {
    val length: Double get() = sqrt((x * x + y * y).toDouble())

    operator fun plus(other: KVector) = KVector(x + other.x, y + other.y)
    operator fun minus(other: KVector) = KVector(x - other.x, y - other.y)
    operator fun times(multiplier: Double) = KVector((x * multiplier).toInt(), (y * multiplier).toInt())
    operator fun div(divisor: Float) = KVector((x / divisor).toInt(), (y / divisor).toInt())
    operator fun unaryMinus() = KVector(-x, -y)

    override fun toString() = "($x, $y)"

    companion object {
        val Empty = KVector(0, 0)

    }
}