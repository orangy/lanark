package org.lanark.events

import org.lanark.application.*

actual abstract class EventKey : Event() {
    actual val frame: Frame
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val keyCode: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val scanCode: UInt
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

}

actual class EventKeyDown : EventKey()
actual class EventKeyUp : EventKey()
actual enum class EventButtonState {
    Pressed,
    Released
}