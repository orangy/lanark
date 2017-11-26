package ksdl.system

import sdl2.*

class KClock {
    val frequency: Long = SDL_GetPerformanceFrequency()
    var start: Long = SDL_GetPerformanceCounter()
        private set

    fun reset() {
        start = SDL_GetPerformanceCounter()
    }

    fun elapsedTicks(): Long = SDL_GetPerformanceCounter() - start

    fun elapsedMillis(): Long {
        val elapsed = elapsedTicks() * 1000
        return (elapsed.toDouble() / frequency).toLong()
    }

    fun elapsedMicros(): Long {
        val elapsed = elapsedTicks() * 1000000
        return (elapsed.toDouble() / frequency).toLong()
    }

    fun elapsedSeconds(): Long {
        return (elapsedTicks().toDouble() / frequency).toLong()
    }
}