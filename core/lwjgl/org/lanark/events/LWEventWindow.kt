package org.lanark.events

import org.lanark.application.*

actual abstract class EventWindow : Event() {
    actual val frame: Frame
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual class EventWindowShown : EventWindow()
actual class EventWindowHidden : EventWindow()
actual class EventWindowExposed : EventWindow()
actual class EventWindowMinimized : EventWindow()
actual class EventWindowMaximized : EventWindow()
actual class EventWindowRestored : EventWindow()
actual class EventWindowMouseEntered : EventWindow()
actual class EventWindowMouseLeft : EventWindow()
actual class EventWindowGotFocus : EventWindow()
actual class EventWindowLostFocus : EventWindow()
actual class EventWindowOfferedFocus : EventWindow()
actual class EventWindowClose : EventWindow()
actual class EventWindowHitTest : EventWindow()

actual class EventWindowMoved : EventWindow() {
    actual val x: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val y: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual class EventWindowResized : EventWindow() {
    actual val width: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val height: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual class EventWindowSizeChanged : EventWindow() {
    actual val width: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val height: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}