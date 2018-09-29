package org.lanark.events

import org.lanark.application.*

actual abstract class EventMouse : Event() {
    actual val frame: Frame
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual abstract class EventMouseButton : EventMouse() {
    actual val button: MouseButton
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val clicks: UInt
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val x: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val y: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val state: EventButtonState
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual class EventMouseButtonDown : EventMouseButton()
actual class EventMouseButtonUp : EventMouseButton()
actual class EventMouseMotion : EventMouse() {
    actual val x: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val y: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val deltaX: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val deltaY: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual class EventMouseWheel : EventMouse() {
    actual val scrollX: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val scrollY: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual enum class MouseButton {
    Left,
    Middle,
    Right,
    X1,
    X2
}