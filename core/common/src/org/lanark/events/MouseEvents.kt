package org.lanark.events

import org.lanark.application.*
import org.lanark.geometry.*

abstract class EventMouse(timestamp: ULong, val frame: Frame) : Event(timestamp)

abstract class EventMousePosition(
    timestamp: ULong,
    frame: Frame,
    val x: Int,
    val y: Int
) : EventMouse(timestamp, frame)

val EventMousePosition.position: Point get() = Point(x, y)

abstract class EventMouseButton(
    timestamp: ULong,
    frame: Frame,
    val button: MouseButton,
    val clicks: UInt,
    x: Int,
    y: Int
) : EventMousePosition(timestamp, frame, x, y)

class EventMouseButtonDown(timestamp: ULong, frame: Frame, button: MouseButton, x: Int, y: Int, clicks: UInt) :
    EventMouseButton(timestamp, frame, button, clicks, x, y)

class EventMouseButtonUp(timestamp: ULong, frame: Frame, button: MouseButton, x: Int, y: Int, clicks: UInt) :
    EventMouseButton(timestamp, frame, button, clicks, x, y)

class EventMouseMotion(
    timestamp: ULong,
    frame: Frame,
    x: Int,
    y: Int,
    val deltaX: Int,
    val deltaY: Int
) : EventMousePosition(timestamp, frame, x, y)

class EventMouseScroll(timestamp: ULong, frame: Frame, val scrollX: Int, val scrollY: Int) :
    EventMouse(timestamp, frame)

enum class MouseButton {
    Left,
    Middle,
    Right,
    X1,
    X2
}
