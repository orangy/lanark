@file:Suppress("NOTHING_TO_INLINE")

package org.lanark.math

import kotlin.math.*

interface Vector3 {
    val x: Float
    val y: Float
    val z: Float

    companion object {
        val Zero = vectorOf(0f, 0f, 0f)
        val One = vectorOf(1f, 1f, 1f)
    }
}

class MutableVector3(override var x: Float, override var y: Float, override var z: Float) : Vector3 {
    constructor(value: Vector3) : this(value.x, value.y, value.z)
    constructor(value: Vector2, z: Float) : this(value.x, value.y, z)

    fun assign(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun translate(value: Vector3) = translate(value.x, value.y, value.z)
    fun translate(dx: Float, dy: Float, dz: Float) {
        x += dx
        y += dy
        z += dz
    }

    fun scale(scaleX: Float, scaleY: Float = scaleX, scaleZ: Float = scaleX) {
        x *= scaleX
        y *= scaleY
        z *= scaleZ
    }

    fun shrink(shrinkX: Float, shrinkY: Float = shrinkX, shrinkZ: Float = shrinkX) {
        x /= shrinkX
        y /= shrinkY
        z /= shrinkZ
    }

    fun normalize() = shrink(length())

    fun negate() {
        assign(-x, -y, -z)
    }
    
    fun reflect(normal: Vector3) {
        val dp = dotProduct(normal)
        assign(x - (2.0f * normal.x) * dp, y - (2.0f * normal.y) * dp, z - (2.0f * normal.z) * dp)
    }
}

inline fun mutableVectorOf(x: Float, y: Float, z: Float, build: MutableVector3.() -> Unit = {}): MutableVector3 {
    return MutableVector3(x, y, z).apply(build)
}

inline fun vectorOf(x: Float, y: Float, z: Float, build: MutableVector3.() -> Unit = {}): Vector3 {
    return MutableVector3(x, y, z).apply(build)
}

inline fun Vector3.toMutableVector(): MutableVector3 = mutableVectorOf(x, y, z)
inline fun Vector3.modify(build: MutableVector3.() -> Unit): Vector3 = mutableVectorOf(x, y, z).apply(build)

inline operator fun MutableVector3.plusAssign(other: Vector3) = translate(other.x, other.y, other.z)
inline operator fun MutableVector3.minusAssign(other: Vector3) = translate(-other.x, -other.y, -other.z)
inline operator fun MutableVector3.timesAssign(value: Float) = scale(value)
inline operator fun MutableVector3.divAssign(value: Float) = shrink(value)

inline operator fun Vector3.component1() = x
inline operator fun Vector3.component2() = y
inline operator fun Vector3.component3() = z

inline operator fun Vector3.times(value: Float) = vectorOf(x * value, y * value, z * value)
inline operator fun Vector3.div(value: Float) = vectorOf(x / value, y / value, z / value)
inline operator fun Vector3.minus(other: Vector3) = vectorOf(x - other.x, y - other.y, z - other.z)
inline operator fun Vector3.unaryMinus() = vectorOf(-x, -y, -z)

inline fun Vector3.length() = sqrt(square(x) + square(y))

inline fun Vector3.normalized() = div(length())
inline fun Vector3.reflected(normal: Vector3) = modify { reflect(normal) }

inline fun min(v1: Vector3, v2: Vector3) = vectorOf(min(v1.x, v2.x), min(v1.y, v2.y), min(v1.z, v2.z))
inline fun max(v1: Vector3, v2: Vector3) = vectorOf(max(v1.x, v2.x), max(v1.y, v2.y), max(v1.z, v2.z))

inline fun Vector3.crossProduct(other: Vector3): Vector3 {
    // capture into locals for performance 
    val x = x
    val y = y
    val z = z
    return vectorOf(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
}

inline fun Vector3.crossProduct(otherX: Float, otherY: Float, otherZ: Float): Vector3 {
    // capture into locals for performance 
    val x = x
    val y = y
    val z = z
    return vectorOf(y * otherZ - z * otherY, z * otherX - x * otherZ, x * otherY - y * otherX)
}

inline fun Vector3.dotProduct(other: Vector3): Float = x * other.x + y * other.y + z * other.z
inline fun Vector3.dotProduct(otherX: Float, otherY: Float, otherZ: Float): Float = x * otherX + y * otherY + z * otherZ
