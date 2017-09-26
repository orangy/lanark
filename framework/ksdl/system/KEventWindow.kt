package ksdl.system

import sdl2.*

abstract class KEventWindow(protected val sdlEvent: SDL_WindowEvent, protected val sdlWindowEventType: SDL_WindowEventID) : KEvent(sdlEvent.type) {
    val window get() = KPlatform.findWindow(sdlEvent.windowID)
    val timestamp get() = sdlEvent.timestamp

    companion object {
        fun createEvent(sdlEvent: SDL_Event): KEventWindow {
            if (sdlEvent.type != SDL_WINDOWEVENT) {
                throw KGraphicsException("KEventWindow.createEvent was called with wrong type of SDL_Event")
            }

            val windowEvent = sdlEvent.window
            val eventKind = SDL_WindowEventID.byValue(windowEvent.event.toInt())
            val kevent = when (eventKind) {
                SDL_WindowEventID.SDL_WINDOWEVENT_NONE -> throw KGraphicsException("SDL_WINDOWEVENT_NONE shouldn't be sent")
                SDL_WindowEventID.SDL_WINDOWEVENT_SHOWN -> KEventWindowShown(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_HIDDEN -> KEventWindowHidden(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_EXPOSED -> KEventWindowExposed(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MOVED -> KEventWindowMoved(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_RESIZED -> KEventWindowResized(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_SIZE_CHANGED -> KEventWindowSizeChanged(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MINIMIZED -> KEventWindowMinimized(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MAXIMIZED -> KEventWindowMaximized(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_RESTORED -> KEventWindowRestored(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_ENTER -> KEventWindowMouseEntered(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_LEAVE -> KEventWindowMouseLeft(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_GAINED -> KEventWindowGotFocus(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_LOST -> KEventWindowLostFocus(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_CLOSE -> KEventWindowClose(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_TAKE_FOCUS -> KEventWindowOfferedFocus(windowEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_HIT_TEST -> KEventWindowHitTest(windowEvent)
            }
            if (kevent.sdlWindowEventType.value != windowEvent.event.toInt())
                logger.error("Posted window event ${kevent.sdlWindowEventType.name} is not the same as in created KWindowEvent instance")
            return kevent
        }
    }

    override fun toString() = "#${sdlEvent.windowID} ${sdlWindowEventType.name}"
}

class KEventWindowShown(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_SHOWN)
class KEventWindowHidden(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_HIDDEN)
class KEventWindowExposed(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_EXPOSED)
class KEventWindowMinimized(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_MINIMIZED)
class KEventWindowMaximized(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_MAXIMIZED)
class KEventWindowRestored(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_RESTORED)
class KEventWindowMouseEntered(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_ENTER)
class KEventWindowMouseLeft(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_LEAVE)
class KEventWindowGotFocus(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_GAINED)
class KEventWindowLostFocus(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_LOST)
class KEventWindowOfferedFocus(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_TAKE_FOCUS)
class KEventWindowClose(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_CLOSE)
class KEventWindowHitTest(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_HIT_TEST)

class KEventWindowMoved(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_MOVED) {
    val x get() = sdlEvent.data1
    val y get() = sdlEvent.data2

    override fun toString() = "${super.toString()} ($x, $y)"
}

class KEventWindowResized(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_RESIZED) {
    val width get() = sdlEvent.data1
    val height get() = sdlEvent.data2

    override fun toString() = "${super.toString()} ($width, $height)"
}

class KEventWindowSizeChanged(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_SIZE_CHANGED) {
    val width get() = sdlEvent.data1
    val height get() = sdlEvent.data2

    override fun toString() = "${super.toString()} ($width, $height)"
}
