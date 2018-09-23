package org.lanark.events

import org.lanark.application.*


expect abstract class EventKey : Event {
    val frame: Frame

    val keyCode: Int
    val scanCode: UInt
}

expect class EventKeyDown : EventKey
expect class EventKeyUp : EventKey

expect enum class EventButtonState {
    Pressed,
    Released
}
