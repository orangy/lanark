package org.lanark.events

import org.lanark.application.*

abstract class EventKey(timestamp: ULong, val frame: Frame, val keyCode: Int, val scanCode: UInt) :
    Event(timestamp) {

    override fun toString() = "${frame.identity} scanCode = $scanCode ${getScanCodeName(scanCode)?:""}, keyCode = $keyCode"
}

class EventKeyDown(timestamp: ULong, frame: Frame, keyCode: Int, scanCode: UInt, val repeat: Boolean) :
    EventKey(timestamp, frame, keyCode, scanCode) {
    override fun toString() = "EventKeyDown: ${super.toString()} ${if (repeat) "[Repeated]" else ""}"

}

class EventKeyUp(timestamp: ULong, frame: Frame, keyCode: Int, scanCode: UInt) :
    EventKey(timestamp, frame, keyCode, scanCode) {
    override fun toString() = "EventKeyUp: ${super.toString()}"
}

enum class EventButtonState {
    Pressed,
    Released
}

expect fun getScanCodeName(scanCode: UInt): String? 
