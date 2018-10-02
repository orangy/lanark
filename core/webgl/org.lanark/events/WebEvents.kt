package org.lanark.events

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*

actual class Events actual constructor(engine: Engine) {
    actual val all: Signal<Event>
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val window: Signal<EventWindow>
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val application: Signal<EventApp>
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val keyboard: Signal<EventKey>
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val mouse: Signal<EventMouse>
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    actual fun poll() {}

    actual companion object {
        actual val LogCategory: LoggerCategory
            get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual fun getScanCodeName(scanCode: UInt): String? {
    return null
}