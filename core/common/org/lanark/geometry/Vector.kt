package org.lanark.geometry

import kotlin.math.*

data class Vector(val x: Int, val y: Int) {
    val length: Double get() = sqrt((x * x + y * y).toDouble())

    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    operator fun times(multiplier: Double) = Vector((x * multiplier).toInt(), (y * multiplier).toInt())
    operator fun div(divisor: Float) = Vector((x / divisor).toInt(), (y / divisor).toInt())
    operator fun unaryMinus() = Vector(-x, -y)

    override fun toString() = "($x, $y)"

    companion object {
        val Empty = Vector(0, 0)

    }
}