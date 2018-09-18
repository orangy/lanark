package ksdl.tests.system

import ksdl.system.*
import sdl2.*
import kotlin.test.*
import kotlin.test.Test

class ClockTest {
    @Test
    fun elapsedTime() {
        val clock = KClock()
        SDL_Delay(200)
        val millis = clock.elapsedMillis()
        assert(millis >= 200u) { "Should be at least 500ms elapsed time" }
        assert(millis < 220u) { "Should not be more than 600ms elapsed time" }
    }
}