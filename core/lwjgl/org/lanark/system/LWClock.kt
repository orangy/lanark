package org.lanark.system

actual class Clock actual constructor() {
    actual var start: ULong
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        private set(value) {}

    actual fun reset() {}
    actual fun delay(millis: ULong) {}
    actual fun elapsedTicks(): ULong {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun elapsedMillis(): ULong {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun elapsedMicros(): ULong {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun elapsedSeconds(): ULong {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}