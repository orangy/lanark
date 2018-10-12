@file:Suppress("NOTHING_TO_INLINE")

package org.lanark.math

interface Matrix4 {
    val data: FloatArray

    companion object {
        const val Size = 4

        val Zero: Matrix4 = MutableMatrix4()
        val One: Matrix4 = MutableMatrix4().also {
            it[0, 0] = 1f
            it[1, 1] = 1f
            it[2, 2] = 1f
            it[3, 3] = 1f
        }

        const val C00 = 0
        const val C10 = 1
        const val C20 = 2
        const val C30 = 3
        const val C01 = 4
        const val C11 = 5
        const val C21 = 6
        const val C31 = 7
        const val C02 = 8
        const val C12 = 9
        const val C22 = 10
        const val C32 = 11
        const val C03 = 12
        const val C13 = 13
        const val C23 = 14
        const val C33 = 15
    }
}

class MutableMatrix4 : Matrix4 {
    override val data = FloatArray(Matrix4.Size * Matrix4.Size)

    operator fun set(x: Int, y: Int, value: Float) {
        data[y * Matrix4.Size + x] = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return data.contentEquals((other as MutableMatrix3).data)
    }

    override fun hashCode() = data.contentHashCode()
}

inline operator fun Matrix4.get(x: Int, y: Int) = data[y * Matrix4.Size + x]

inline fun Vector3.multiplied(matrix: Matrix4): Vector3 = modify { multiply(matrix) }
fun MutableVector3.multiply(matrix: Matrix4) {
    // capture into locals for performance 
    val m = matrix.data
    val x = x
    val y = y
    val z = z
    assign(
        x * m[Matrix4.C00] + y * m[Matrix4.C01] + z * m[Matrix4.C02] + m[Matrix4.C03],
        x * m[Matrix4.C10] + y * m[Matrix4.C11] + z * m[Matrix4.C12] + m[Matrix4.C13],
        x * m[Matrix4.C20] + y * m[Matrix4.C21] + z * m[Matrix4.C22] + m[Matrix4.C23]
    )
}
