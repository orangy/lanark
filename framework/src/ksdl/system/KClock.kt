package ksdl.system

import sdl2.*

class KClock {
    val frequency: Long = SDL_GetPerformanceFrequency()
    var start: Long = SDL_GetPerformanceCounter()
        private set

    fun reset() {
        start = SDL_GetPerformanceCounter()
    }

    fun elapsed(): Long = SDL_GetPerformanceCounter() - start

    fun elapsedMillis(): Long {
        val elapsed = elapsed() * 1000
        return (elapsed.toDouble() / frequency).toLong()
    }

    fun elapsedSeconds(): Long {
        return (elapsed().toDouble() / frequency).toLong()
    }
}