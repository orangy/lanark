package org.lanark.events

import org.lanark.application.*

actual abstract class EventWindow(actual val frame: Frame) : Event() {
}

actual class EventWindowShown(frame: Frame) : EventWindow(frame)
actual class EventWindowHidden(frame: Frame) : EventWindow(frame)
actual class EventWindowExposed(frame: Frame) : EventWindow(frame)
actual class EventWindowMinimized(frame: Frame) : EventWindow(frame)
actual class EventWindowMaximized(frame: Frame) : EventWindow(frame)
actual class EventWindowRestored(frame: Frame) : EventWindow(frame)
actual class EventWindowMouseEntered(frame: Frame) : EventWindow(frame)
actual class EventWindowMouseLeft(frame: Frame) : EventWindow(frame)
actual class EventWindowGotFocus(frame: Frame) : EventWindow(frame)
actual class EventWindowLostFocus(frame: Frame) : EventWindow(frame)
actual class EventWindowOfferedFocus(frame: Frame) : EventWindow(frame)
actual class EventWindowClose(frame: Frame) : EventWindow(frame)
actual class EventWindowHitTest(frame: Frame) : EventWindow(frame)
actual class EventWindowMoved(frame: Frame, actual val x: Int, actual val y: Int) : EventWindow(frame)
actual class EventWindowResized(frame: Frame, actual val width: Int, actual val height: Int) : EventWindow(frame)
actual class EventWindowSizeChanged(frame: Frame, actual val width: Int, actual val height: Int) : EventWindow(frame) 