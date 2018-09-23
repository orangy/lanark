package org.lanark.events

import org.lanark.application.*
import org.lanark.diagnostics.*
import sdl2.*

actual abstract class EventWindow(
    actual val frame: Frame,
    protected val sdlEvent: SDL_WindowEvent,
    protected val sdlWindowEventType: SDL_WindowEventID
) : Event(sdlEvent.timestamp.toULong()) {
    companion object {
        fun createEvent(sdlEvent: SDL_Event, engine: Engine): EventWindow {
            if (sdlEvent.type != SDL_WINDOWEVENT) {
                throw PlatformException("EventWindow.createEvent was called with unknown type of SDL_Event")
            }

            val windowEvent = sdlEvent.window
            val frame = engine.getFrame(windowEvent.windowID)

            val eventKind = SDL_WindowEventID.byValue(windowEvent.event.toUInt())
            val kevent = when (eventKind) {
                SDL_WindowEventID.SDL_WINDOWEVENT_NONE -> throw PlatformException("SDL_WINDOWEVENT_NONE shouldn't be sent")
                SDL_WindowEventID.SDL_WINDOWEVENT_SHOWN -> EventWindowShown(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_HIDDEN -> EventWindowHidden(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_EXPOSED -> EventWindowExposed(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MOVED -> EventWindowMoved(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_RESIZED -> EventWindowResized(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_SIZE_CHANGED -> EventWindowSizeChanged(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MINIMIZED -> EventWindowMinimized(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MAXIMIZED -> EventWindowMaximized(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_RESTORED -> EventWindowRestored(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_ENTER -> EventWindowMouseEntered(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_LEAVE -> EventWindowMouseLeft(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_GAINED -> EventWindowGotFocus(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_LOST -> EventWindowLostFocus(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_CLOSE -> EventWindowClose(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_TAKE_FOCUS -> EventWindowOfferedFocus(frame, windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_HIT_TEST -> EventWindowHitTest(frame, windowEvent)
            }
            if (kevent.sdlWindowEventType.value != windowEvent.event.toUInt())
                engine.logger.error("Posted window event ${kevent.sdlWindowEventType.name} is not the same as in the created EventWindow instance")
            return kevent
        }
    }

    override fun toString() = "${sdlWindowEventType.name} Window #${sdlEvent.windowID}"
}

actual class EventWindowShown(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_SHOWN)

actual class EventWindowHidden(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_HIDDEN)

actual class EventWindowExposed(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_EXPOSED)

actual class EventWindowMinimized(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_MINIMIZED)

actual class EventWindowMaximized(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_MAXIMIZED)

actual class EventWindowRestored(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_RESTORED)

actual class EventWindowMouseEntered(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_ENTER)

actual class EventWindowMouseLeft(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_LEAVE)

actual class EventWindowGotFocus(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_GAINED)

actual class EventWindowLostFocus(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_LOST)

actual class EventWindowOfferedFocus(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_TAKE_FOCUS)

actual class EventWindowClose(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_CLOSE)

actual class EventWindowHitTest(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_HIT_TEST)

actual class EventWindowMoved(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_MOVED) {
    actual val x: Int get() = sdlEvent.data1
    actual val y: Int get() = sdlEvent.data2

    override fun toString() = "${super.toString()} to ($x, $y)"
}

actual class EventWindowResized(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_RESIZED) {
    actual val width get() = sdlEvent.data1
    actual val height get() = sdlEvent.data2

    override fun toString() = "${super.toString()} to ($width, $height)"
}

actual class EventWindowSizeChanged(frame: Frame, sdlEvent: SDL_WindowEvent) :
    EventWindow(frame, sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_SIZE_CHANGED) {
    actual val width get() = sdlEvent.data1
    actual val height get() = sdlEvent.data2

    override fun toString() = "${super.toString()} to ($width, $height)"
}
