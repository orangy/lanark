@file:Suppress("NOTHING_TO_INLINE")

package org.lanark.math

import kotlin.math.*
import kotlin.random.*

interface Vector2 {
    val x: Float
    val y: Float

    companion object {
        val Zero = vectorOf(0f, 0f)
        val One = vectorOf(1f, 1f)

        fun random(length: Float = 1f, random: Random = Random): Vector2 = vectorOf(length, length) {
            rotate(random.nextFloat(0f, PI2f))
        }
    }
}

data class MutableVector2(override var x: Float, override var y: Float) : Vector2 {
    constructor(value: Vector2) : this(value.x, value.y)

    fun assign(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun translate(value: Vector2) = translate(value.x, value.y)
    fun translate(dx: Float, dy: Float) {
        x += dx
        y += dy
    }

    fun scale(scaleX: Float, scaleY: Float = scaleX) {
        x *= scaleX
        y *= scaleY
    }

    fun shrink(shrinkX: Float, shrinkY: Float = shrinkX) {
        x /= shrinkX
        y /= shrinkY
    }

    fun normalize() = shrink(length())
    fun negate() {
        assign(-x, -y)
    }

    fun rotate(radians: Float) {
        val cos = cos(radians)
        val sin = sin(radians)
        val (oldX, oldY) = this
        x = oldX * cos - oldY * sin
        y = oldX * sin + oldY * cos
    }
}

inline fun mutableVectorOf(x: Float, y: Float, build: MutableVector2.() -> Unit = {}): MutableVector2 {
    return MutableVector2(x, y).apply(build)
}

inline fun vectorOf(x: Float, y: Float, build: MutableVector2.() -> Unit = {}): Vector2 {
    return MutableVector2(x, y).apply(build)
}

inline fun Vector2.toMutableVector(): MutableVector2 = mutableVectorOf(x, y)
inline fun Vector2.modify(build: MutableVector2.() -> Unit): Vector2 = mutableVectorOf(x, y).apply(build)

inline operator fun MutableVector2.plusAssign(other: Vector2) = translate(other.x, other.y)
inline operator fun MutableVector2.minusAssign(other: Vector2) = translate(-other.x, -other.y)
inline operator fun MutableVector2.timesAssign(value: Float) = scale(value)
inline operator fun MutableVector2.divAssign(value: Float) = shrink(value)

inline operator fun Vector2.component1() = x
inline operator fun Vector2.component2() = y

inline operator fun Vector2.times(value: Float) = vectorOf(x * value, y * value)
inline operator fun Vector2.div(value: Float) = vectorOf(x / value, y / value)
inline operator fun Vector2.minus(other: Vector2) = vectorOf(x - other.x, y - other.y)
inline operator fun Vector2.unaryMinus() = vectorOf(-x, -y)

inline fun Vector2.length(): Float = sqrt(square(x) + square(y))
inline fun Vector2.normalized(): Vector2 = div(length())
inline fun Vector2.dotProduct(other: Vector2): Float = x * other.x + y * other.y
inline fun Vector2.dotProduct(otherX: Float, otherY: Float): Float = x * otherX + y * otherY

inline fun Vector2.crossProduct(other: Vector2): Float = x * other.y - y * other.x
inline fun Vector2.crossProduct(otherX: Float, otherY: Float): Float = x * otherY - y * otherX
fun Vector2.angleTo(other: Vector2): Float = atan2(crossProduct(other), dotProduct(other)) 


