package ksdl.system

import sdl2.*

class KClock {
    val frequency: ULong = SDL_GetPerformanceFrequency()
    var start: ULong = SDL_GetPerformanceCounter()
        private set

    fun reset() {
        start = SDL_GetPerformanceCounter()
    }

    fun elapsedTicks(): ULong = SDL_GetPerformanceCounter() - start

    fun elapsedMillis(): ULong {
        val elapsed = elapsedTicks() * 1000u
        return (elapsed / frequency)
    }

    fun elapsedMicros(): ULong {
        val elapsed = elapsedTicks() * 1000000u
        return (elapsed / frequency)
    }

    fun elapsedSeconds(): ULong {
        return (elapsedTicks() / frequency)
    }
}