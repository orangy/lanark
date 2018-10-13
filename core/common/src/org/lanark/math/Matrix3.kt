@file:Suppress("NOTHING_TO_INLINE")

package org.lanark.math

import org.lanark.application.*
import org.lanark.math.Matrix3.Companion.C00
import org.lanark.math.Matrix3.Companion.C01
import org.lanark.math.Matrix3.Companion.C02
import org.lanark.math.Matrix3.Companion.C10
import org.lanark.math.Matrix3.Companion.C11
import org.lanark.math.Matrix3.Companion.C12
import org.lanark.math.Matrix3.Companion.C20
import org.lanark.math.Matrix3.Companion.C21
import org.lanark.math.Matrix3.Companion.C22
import kotlin.math.*

interface Matrix3 {
    val data: FloatArray

    companion object {
        const val Size = 3

        val Zero: Matrix3 = MutableMatrix3()
        val One: Matrix3 = MutableMatrix3().apply {
            val data = data
            data[C00] = 1f
            data[C11] = 1f
            data[C22] = 1f
        }

        const val C00 = 0
        const val C10 = 1
        const val C20 = 2
        const val C01 = 3
        const val C11 = 4
        const val C21 = 5
        const val C02 = 6
        const val C12 = 7
        const val C22 = 8

        fun translate(x: Float, y: Float): Matrix3 = MutableMatrix3().apply {
            val data = data
            data[C00] = 1f
            data[C11] = 1f
            data[C02] = x
            data[C12] = y
            data[C22] = 1f
        }

        fun scale(scaleX: Float, scaleY: Float): Matrix3 = MutableMatrix3().apply {
            data[C00] = scaleX
            data[C11] = scaleY
            data[C22] = 1f
        }

        fun rotate(angle: Float): Matrix3 = MutableMatrix3().apply {
            val cos = cos(angle)
            val sin = sin(angle)
            val data = data
            data[C00] = cos
            data[C10] = sin
            data[C20] = 0f

            data[C01] = -sin
            data[C11] = cos
            data[C21] = 0f

            data[C02] = 0f
            data[C12] = 0f
            data[C22] = 1f
        }

        fun fromValues(values: FloatArray): Matrix3 {
            return MutableMatrix3(values)
        }
    }
}

class MutableMatrix3() : Matrix3 {

    constructor(matrix: Matrix3) : this() {
        matrix.data.copyInto(data)
    }

    constructor(values: FloatArray) : this() {
        values.copyInto(data)
    }

    override val data = FloatArray(Matrix3.Size * Matrix3.Size)

    operator fun set(x: Int, y: Int, value: Float) {
        data[y * Matrix3.Size + x] = value
    }

    fun multiply(other: Matrix3) {
        // capture arrays into locals for performance
        val m1 = data
        val m2 = other.data

        val v00 = m1[C00] * m2[C00] + m1[C01] * m2[C10] + m1[C02] * m2[C20]
        val v01 = m1[C00] * m2[C01] + m1[C01] * m2[C11] + m1[C02] * m2[C21]
        val v02 = m1[C00] * m2[C02] + m1[C01] * m2[C12] + m1[C02] * m2[C22]

        val v10 = m1[C10] * m2[C00] + m1[C11] * m2[C10] + m1[C12] * m2[C20]
        val v11 = m1[C10] * m2[C01] + m1[C11] * m2[C11] + m1[C12] * m2[C21]
        val v12 = m1[C10] * m2[C02] + m1[C11] * m2[C12] + m1[C12] * m2[C22]

        val v20 = m1[C20] * m2[C00] + m1[C21] * m2[C10] + m1[C22] * m2[C20]
        val v21 = m1[C20] * m2[C01] + m1[C21] * m2[C11] + m1[C22] * m2[C21]
        val v22 = m1[C20] * m2[C02] + m1[C21] * m2[C12] + m1[C22] * m2[C22]

        m1[C00] = v00; m1[C10] = v10; m1[C20] = v20
        m1[C01] = v01; m1[C11] = v11; m1[C21] = v21
        m1[C02] = v02; m1[C12] = v12; m1[C22] = v22
    }

    fun transpose() {
        val data = data
        val v01 = data[C10]
        val v02 = data[C20]
        val v10 = data[C01]
        val v12 = data[C21]
        val v20 = data[C02]
        val v21 = data[C12]
        data[C01] = v01
        data[C02] = v02
        data[C10] = v10
        data[C12] = v12
        data[C20] = v20
        data[C21] = v21
    }

    fun normalize() {
        val det = determinant()
        for (index in 0 until data.size) {
            data[index] /= det
        }
    }

    fun add(other: Matrix3) {
        for (index in 0 until data.size) {
            data[index] += other.data[index]
        }
    }

    fun subtract(other: Matrix3) {
        for (index in 0 until data.size) {
            data[index] -= other.data[index]
        }
    }

    fun scale(scaleX: Float, scaleY: Float = scaleX) {
        multiply(Matrix3.scale(scaleX, scaleY))
    }


    fun translate(x: Float, y: Float) {
        if (x == 0f && y == 0f)
            return
        multiply(Matrix3.translate(x, y))
    }

    fun invert() {
        val det = determinant()
        if (det == 0f)
            throw EngineException("Can't invert a singular matrix")

        val data = data
        val a00 = data[C11] * data[C22] - data[C21] * data[C12]
        val a10 = data[C20] * data[C12] - data[C10] * data[C22]
        val a20 = data[C10] * data[C21] - data[C20] * data[C11]
        val a01 = data[C21] * data[C02] - data[C01] * data[C22]
        val a11 = data[C00] * data[C22] - data[C20] * data[C02]
        val a21 = data[C20] * data[C01] - data[C00] * data[C21]
        val a02 = data[C01] * data[C12] - data[C11] * data[C02]
        val a12 = data[C10] * data[C02] - data[C00] * data[C12]
        val a22 = data[C00] * data[C11] - data[C10] * data[C01]

        data[C00] = a00 / det
        data[C10] = a10 / det
        data[C20] = a20 / det
        data[C01] = a01 / det
        data[C11] = a11 / det
        data[C21] = a21 / det
        data[C02] = a02 / det
        data[C12] = a12 / det
        data[C22] = a22 / det
    }

    fun rotate(angle: Float) {
        if (angle == 0f)
            return
        multiply(Matrix3.rotate(angle))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return data.contentEquals((other as MutableMatrix3).data)
    }

    override fun hashCode() = data.contentHashCode()
    override fun toString() = "Matrix3[${data.joinToString()}]"
}

inline fun Matrix3.modify(build: MutableMatrix3.() -> Unit): Matrix3 = MutableMatrix3(this).apply(build)

operator fun Matrix3.get(x: Int, y: Int) = data[y * Matrix3.Size + x]

fun Matrix3.determinant(): Float {
    val data = data
    return data[C00] * data[C11] * data[C22] +
            data[C01] * data[C12] * data[C20] +
            data[C02] * data[C10] * data[C21] -
            data[C00] * data[C12] * data[C21] -
            data[C01] * data[C10] * data[C22] -
            data[C02] * data[C11] * data[C20]
}

fun Matrix3.trace(): Float {
    val data = data
    return data[C00] + data[C11] + data[C22]
}

fun Matrix3.translation(): Vector2 {
    val data = data
    return vectorOf(data[C02], data[C12])
}

fun Matrix3.rotation(): Float {
    val data = data
    val x = data[C21] - data[C12]
    val y = data[C02] - data[C20]
    val z = data[C10] - data[C01]
    val r = sqrt(square(x) + square(y) + square(z))
    val t = data[C00] + data[C11] + data[C22]
    return atan2(r, t - 1)
}

fun Matrix3.scale(): Vector2 {
    val data = data
    return vectorOf(
        sqrt(square(data[C00]) + square(data[C01])),
        sqrt(square(data[C10]) + square(data[C11]))
    )
}

inline fun Matrix3.translated(x: Float, y: Float): Matrix3 = modify { translate(x, y) }
inline fun Matrix3.translated(vector: Vector2): Matrix3 = modify { translate(vector.x, vector.y) }
inline fun Matrix3.transposed(): Matrix3 = modify { transpose() }
inline fun Matrix3.normalized(): Matrix3 = modify { normalize() }
inline fun Matrix3.rotated(angle: Float): Matrix3 = modify { rotate(angle) }
inline fun Matrix3.inverted(): Matrix3 = modify { invert() }

inline fun Vector3.multiplied(matrix: Matrix3): Vector3 = modify { multiply(matrix) }
inline fun Matrix3.multiplied(matrix: Matrix3): Matrix3 = modify { multiply(matrix) }

fun MutableVector3.multiply(matrix: Matrix3) {
    // capture into locals for performance 
    val m = matrix.data
    val x = x
    val y = y
    val z = z
    assign(
        x * m[Matrix3.C00] + y * m[Matrix3.C01] + z * m[Matrix3.C02],
        x * m[Matrix3.C10] + y * m[Matrix3.C11] + z * m[Matrix3.C12],
        x * m[Matrix3.C20] + y * m[Matrix3.C21] + z * m[Matrix3.C22]
    )
}

fun MutableVector2.multiply(matrix: Matrix3) {
    // capture into locals for performance 
    val data = matrix.data
    val x = x
    val y = y
    assign(
        x * data[Matrix3.C00] + y * data[Matrix3.C01] + data[Matrix3.C02],
        x * data[Matrix3.C10] + y * data[Matrix3.C11] + data[Matrix3.C12]
    )
}

fun Matrix3.copy(): Matrix3 = MutableMatrix3(this)
