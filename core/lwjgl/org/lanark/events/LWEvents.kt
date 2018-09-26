package org.lanark.events

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*

actual class Events actual constructor(engine: Engine) {
    actual val all: Signal<Event> = Signal("Event")
    actual val window: Signal<EventWindow> = all.filter()
    actual val application: Signal<EventApp> = all.filter()
    actual val keyboard: Signal<EventKey> = all.filter()
    actual val mouse: Signal<EventMouse> = all.filter()

    actual fun poll() {}

    actual companion object {
        actual val LogCategory = LoggerCategory("Events")
    }
}