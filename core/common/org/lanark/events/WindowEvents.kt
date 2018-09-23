package org.lanark.events

import org.lanark.application.*

expect abstract class EventWindow : Event {
    val frame: Frame
} 

expect class EventWindowShown : EventWindow
expect class EventWindowHidden : EventWindow
expect class EventWindowExposed : EventWindow
expect class EventWindowMinimized : EventWindow
expect class EventWindowMaximized : EventWindow
expect class EventWindowRestored : EventWindow
expect class EventWindowMouseEntered : EventWindow
expect class EventWindowMouseLeft : EventWindow
expect class EventWindowGotFocus : EventWindow
expect class EventWindowLostFocus : EventWindow
expect class EventWindowOfferedFocus : EventWindow
expect class EventWindowClose : EventWindow
expect class EventWindowHitTest : EventWindow

expect class EventWindowMoved : EventWindow {
    val x: Int
    val y: Int
}

expect class EventWindowResized : EventWindow {
    val width: Int
    val height: Int
}

expect class EventWindowSizeChanged : EventWindow {
    val width: Int
    val height: Int
}

