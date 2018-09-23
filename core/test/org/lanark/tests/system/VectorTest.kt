package org.lanark.tests.system

import org.lanark.geometry.*
import org.lanark.system.*
import kotlin.test.*

class VectorTest {
    @Test
    fun vectorLength() {
        assertEquals(0.0, Vector.Empty.length)
        assertEquals(1.414, Math.round(Vector(1, 1).length, 3))
    }

    @Test
    fun vectorSum() {
        assertEquals(Vector(1, 1), Vector.Empty + Vector(1, 1))
    }

}