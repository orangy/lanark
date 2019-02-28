package org.lanark.system

import kotlin.js.*

actual class Clock actual constructor() {
    private fun currentTime() = Date().getTime().toLong().toULong()
    private val frequency = 1000u

    actual fun delay(millis: ULong) {}

    actual var start: ULong = currentTime()
        private set

    actual fun reset() {
        start = currentTime()
    }

    actual fun elapsedTicks(): ULong = currentTime() - start

    actual fun elapsedMillis(): ULong {
        val elapsed = elapsedTicks() * 1000u
        return (elapsed / frequency)
    }

    actual fun elapsedMicros(): ULong {
        val elapsed = elapsedTicks() * 1000_000u
        return (elapsed / frequency)
    }

    actual fun elapsedSeconds(): ULong {
        return (elapsedTicks() / frequency)
    }
}