package org.lanark.tests.system

import org.lanark.system.*
import sdl2.*
import kotlin.test.*

class ClockTest {
    @Test
    fun elapsedTime() {
        val clock = Clock()
        SDL_Delay(200)
        val millis = clock.elapsedMillis()
        assert(millis >= 200u) { "Should be at least 500ms elapsed time" }
        assert(millis < 220u) { "Should not be more than 600ms elapsed time" }
    }
}