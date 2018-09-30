package org.lanark.events

import org.lanark.application.*

actual abstract class EventMouse(actual val frame: Frame) : Event()

actual abstract class EventMouseButton(frame: Frame, actual val button: MouseButton, actual val x: Int, actual val y: Int) : EventMouse(frame) {
    actual val clicks: UInt
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val state: EventButtonState
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual class EventMouseButtonDown(frame: Frame, button: MouseButton, x: Int, y: Int) : EventMouseButton(frame, button, x, y)
actual class EventMouseButtonUp(frame: Frame, button: MouseButton, x: Int, y: Int) : EventMouseButton(frame, button, x, y)
actual class EventMouseMotion(frame: Frame, actual val x: Int, actual val y: Int) : EventMouse(frame) {
    actual val deltaX: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val deltaY: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual class EventMouseWheel(frame: Frame, actual val scrollX: Int, actual val scrollY: Int) : EventMouse(frame)

actual enum class MouseButton {
    Left,
    Middle,
    Right,
    X1,
    X2
}