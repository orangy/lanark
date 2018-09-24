package org.lanark.system

actual class Clock actual constructor() {
    actual var start: ULong = System.nanoTime().toULong()
        private set

    actual fun reset() {
        start = System.nanoTime().toULong()
    }

    actual fun delay(millis: ULong) {
        Thread.sleep(millis.toLong())
    }
    
    actual fun elapsedTicks(): ULong = System.nanoTime().toULong() - start

    actual fun elapsedMillis(): ULong {
        return elapsedTicks() / 1000_000u
    }

    actual fun elapsedMicros(): ULong {
        return elapsedTicks() / 1000u
    }

    actual fun elapsedSeconds(): ULong {
        return elapsedTicks() / 1000_000_000u
    }

}