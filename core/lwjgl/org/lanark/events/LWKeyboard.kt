package org.lanark.events

import org.lanark.application.*

actual abstract class EventKey(actual val frame: Frame, actual val keyCode: Int, actual val scanCode: UInt) : Event()
actual class EventKeyDown(frame: Frame, keyCode: Int, scanCode: UInt, val repeat: Boolean) :
    EventKey(frame, keyCode, scanCode)

actual class EventKeyUp(frame: Frame, keyCode: Int, scanCode: UInt) :
    EventKey(frame, keyCode, scanCode)

actual enum class EventButtonState {
    Pressed,
    Released
}