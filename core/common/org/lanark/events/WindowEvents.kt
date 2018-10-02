package org.lanark.events

import org.lanark.application.*

abstract class EventWindow(timestamp: ULong, val frame: Frame) : Event(timestamp) {

}

class EventWindowShown(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowHidden(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowExposed(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowMinimized(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowMaximized(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowRestored(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowMouseEntered(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowMouseLeft(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowGotFocus(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowLostFocus(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowOfferedFocus(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowClose(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)
class EventWindowHitTest(timestamp: ULong, frame: Frame) : EventWindow(timestamp, frame)

class EventWindowMoved(
    timestamp: ULong, frame: Frame,
    val x: Int,
    val y: Int
) : EventWindow(timestamp, frame) {
}

class EventWindowResized(
    timestamp: ULong, frame: Frame,
    val width: Int,
    val height: Int
) : EventWindow(timestamp, frame) {
}

class EventWindowSizeChanged(
    timestamp: ULong, frame: Frame,
    val width: Int,
    val height: Int
) : EventWindow(timestamp, frame) {
}

