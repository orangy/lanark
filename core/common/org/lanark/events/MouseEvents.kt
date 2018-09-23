package org.lanark.events

import org.lanark.application.*
import org.lanark.geometry.*

expect abstract class EventMouse : Event {
    val frame: Frame
}

expect abstract class EventMouseButton : EventMouse {
    val button: MouseButton
    val clicks: UInt
    val x: Int
    val y: Int
    val state: EventButtonState
}

val EventMouseButton.position: Point get() = Point(x, y)

expect class EventMouseButtonDown : EventMouseButton
expect class EventMouseButtonUp : EventMouseButton

expect class EventMouseMotion : EventMouse {
    val x: Int
    val y: Int
    val deltaX: Int
    val deltaY: Int
}

val EventMouseMotion.position: Point get() = Point(x, y)


expect class EventMouseWheel : EventMouse {
    val scrollX: Int
    val scrollY: Int
}

expect enum class MouseButton {
    Left,
    Middle,
    Right,
    X1,
    X2
}
