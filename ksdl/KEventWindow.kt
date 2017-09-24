package ksdl

import sdl2.*

abstract class KEventWindow(protected val sdlEvent: SDL_WindowEvent, protected val sdlEventID: SDL_WindowEventID) : KEvent(SDL_WINDOWEVENT) {
    val window get() = KGraphics.findWindow(sdlEvent.windowID)
    val timestamp get() = sdlEvent.timestamp

    companion object {
        fun createEventWindow(sdlEvent: SDL_WindowEvent): KEventWindow {
            val eventKind = SDL_WindowEventID.byValue(sdlEvent.event.toInt())
            val kevent = when (eventKind) {
                SDL_WindowEventID.SDL_WINDOWEVENT_NONE -> throw KGraphicsException("SDL_WINDOWEVENT_NONE shouldn't be sent")
                SDL_WindowEventID.SDL_WINDOWEVENT_SHOWN -> KEventWindowShown(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_HIDDEN -> KEventWindowHidden(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_EXPOSED -> KEventWindowExposed(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MOVED -> KEventWindowMoved(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_RESIZED -> KEventWindowResized(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_SIZE_CHANGED -> KEventWindowSizeChanged(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MINIMIZED -> KEventWindowMinimized(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_MAXIMIZED -> KEventWindowMaximized(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_RESTORED -> KEventWindowRestored(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_ENTER -> KEventWindowMouseEntered(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_LEAVE -> KEventWindowMouseLeft(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_GAINED -> KEventWindowGotFocus(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_LOST -> KEventWindowLostFocus(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_CLOSE -> KEventWindowClose(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_TAKE_FOCUS -> KEventWindowOfferedFocus(sdlEvent)
                SDL_WindowEventID.SDL_WINDOWEVENT_HIT_TEST -> KEventWindowHitTest(sdlEvent)
            }
            if (kevent.sdlEventID.value != sdlEvent.event.toInt())
                logger.error("Posted window event ${kevent.sdlEventID.name} is not the same as in created KWindowEvent instance")
            return kevent
        }
    }

    override fun toString() = "${sdlEventID.name} #${sdlEvent.windowID}"
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

    override fun toString() = super.toString() + " ($x, $y)"
}

class KEventWindowResized(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_RESIZED) {
    val width get() = sdlEvent.data1
    val height get() = sdlEvent.data2

    override fun toString() = super.toString() + " ($width, $height)"
}

class KEventWindowSizeChanged(sdlEvent: SDL_WindowEvent) : KEventWindow(sdlEvent, SDL_WindowEventID.SDL_WINDOWEVENT_SIZE_CHANGED) {
    val width get() = sdlEvent.data1
    val height get() = sdlEvent.data2

    override fun toString() = super.toString() + " ($width, $height)"
}
