package ksdl.tests.system

import ksdl.geometry.*
import ksdl.system.*
import kotlin.test.*

class VectorTest {
    @Test
    fun vectorLength() {
        assertEquals(0.0, KVector.Empty.length)
        assertEquals(1.414, KMath.round(KVector(1, 1).length, 3))
    }

    @Test
    fun vectorSum() {
        assertEquals(KVector(1, 1), KVector.Empty + KVector(1, 1))
    }

}