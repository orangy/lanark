package org.lanark.tests.system

import org.lanark.system.*
import kotlin.test.*

class ClockTest {
    @Test
    fun elapsedTime() {
        val clock = Clock()
        clock.delay(200u)
        val millis = clock.elapsedMillis()
        assertTrue(millis >= 200u, "Should be at least 200ms elapsed time")
        assertTrue(millis < 220u, "Should not be more than 220ms elapsed time")
    }
}