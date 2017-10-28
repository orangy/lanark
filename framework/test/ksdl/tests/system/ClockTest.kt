package ksdl.tests.system

import ksdl.system.*
import sdl2.*
import kotlin.test.*
import kotlin.test.Test

class ClockTest {
    @Test
    fun `500ms delay should cause at least 500ms elapsedMillis`() {
        val clock = KClock()
        SDL_Delay(500)
        val millis = clock.elapsedMillis()
        assert(millis >= 500) { "Should be at least 500ms elapsed time" }
        assert(millis < 600) { "Should not be more than 600ms elapsed time" }
    }
}