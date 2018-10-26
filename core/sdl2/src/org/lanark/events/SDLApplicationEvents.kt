package org.lanark.events

import org.lanark.application.*
import sdl2.*

@Suppress("UNUSED_PARAMETER")
fun createAppEvent(sdlEvent: SDL_Event, engine: Engine): EventApp {
    val timestamp = sdlEvent.common.timestamp.toULong()
    val type = sdlEvent.type
    return when (type) {
        SDL_QUIT -> EventAppQuit(timestamp)
        SDL_APP_TERMINATING -> EventAppTerminating(timestamp)
        SDL_APP_LOWMEMORY -> EventAppLowMemory(timestamp)
        SDL_APP_DIDENTERBACKGROUND -> EventAppEnteredBackground(timestamp)
        SDL_APP_DIDENTERFOREGROUND -> EventAppEnteredForeground(timestamp)
        SDL_APP_WILLENTERBACKGROUND -> EventAppEnteringBackground(timestamp)
        SDL_APP_WILLENTERFOREGROUND -> EventAppEnteringForeground(timestamp)
        else -> throw EngineException("EventApp.createEvent was called with unknown type of SDL_Event")
    }
}

fun createWindowEvent(sdlEvent: SDL_Event, engine: Engine): EventWindow {
    val timestamp = sdlEvent.common.timestamp.toULong()
    val windowEvent = sdlEvent.window
    val frame = engine.getFrame(windowEvent.windowID)

    val eventKind = SDL_WindowEventID.byValue(windowEvent.event.toUInt())
    return when (eventKind) {
        SDL_WindowEventID.SDL_WINDOWEVENT_NONE -> throw EngineException("SDL_WINDOWEVENT_NONE shouldn't be sent")
        SDL_WindowEventID.SDL_WINDOWEVENT_SHOWN -> EventWindowShown(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_HIDDEN -> EventWindowHidden(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_EXPOSED -> EventWindowExposed(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_MOVED -> EventWindowMoved(
            timestamp,
            frame,
            windowEvent.data1,
            windowEvent.data2
        )
        SDL_WindowEventID.SDL_WINDOWEVENT_RESIZED -> EventWindowResized(
            timestamp,
            frame,
            windowEvent.data1,
            windowEvent.data2
        )
        SDL_WindowEventID.SDL_WINDOWEVENT_SIZE_CHANGED -> EventWindowSizeChanged(
            timestamp,
            frame,
            windowEvent.data1,
            windowEvent.data2
        )
        SDL_WindowEventID.SDL_WINDOWEVENT_MINIMIZED -> EventWindowMinimized(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_MAXIMIZED -> EventWindowMaximized(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_RESTORED -> EventWindowRestored(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_ENTER -> EventWindowMouseEntered(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_LEAVE -> EventWindowMouseLeft(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_GAINED -> EventWindowGotFocus(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_FOCUS_LOST -> EventWindowLostFocus(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_CLOSE -> EventWindowClose(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_TAKE_FOCUS -> EventWindowOfferedFocus(timestamp, frame)
        SDL_WindowEventID.SDL_WINDOWEVENT_HIT_TEST -> EventWindowHitTest(timestamp, frame)
    }
}
