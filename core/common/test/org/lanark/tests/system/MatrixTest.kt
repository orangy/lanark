package org.lanark.tests.system

import org.lanark.math.*
import kotlin.test.*

class MatrixTest {
    @Test
    fun testMultiplication() {
        val original = createMatrix()
        val m1 = original.copy()
        val m2 = original.copy()

        // Multiply two matrices together producing a new result matrix.
        val product = m1.multiplied(m2)

        assertNotSame(m1, product)
        assertNotSame(m2, product)

        assertEquals(original, m1)
        assertEquals(original, m2)

        val expectedValues = floatArrayOf(
            5f, 8f, 11f,
            8f, 14f, 20f,
            11f, 20f, 29f
        )
        assertEquals(Matrix3.fromValues(expectedValues), product)
    }


    @Test
    fun testBuilders() {
        val original = createMatrix()
        val modified = original.modify { 
            rotate(PIf/2)
            scale(2f)
            translate(5f, -3f)
        }
        
        // TODO: check the values, currently they are just fixing current behavior
        assertEquals(vectorOf(2f, 4.472136f), modified.scale())
        assertEquals(vectorOf(12f, 29f), modified.translation())
        assertEquals(0.64390904f, modified.rotation())
    }

    private fun createMatrix(): MutableMatrix3 {
        val original = MutableMatrix3()

        //  0, 1, 2
        //  1, 2, 3
        //  2, 3, 4
        repeat(3) { x -> repeat(3) { y -> original[x, y] = (x + y).toFloat() } }
        return original
    }
}

