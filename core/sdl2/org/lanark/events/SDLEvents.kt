package org.lanark.events

import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import sdl2.*

actual class Events actual constructor(private val engine: Engine) {
    actual val all: Signal<Event> = Signal("Event")
    actual val window: Signal<EventWindow> = all.filter()
    actual val application: Signal<EventApp> = all.filter()
    actual val keyboard: Signal<EventKey> = all.filter()
    actual val mouse: Signal<EventMouse> = all.filter()

    actual fun poll() = memScoped {
        val event = alloc<SDL_Event>()
        while (SDL_PollEvent(event.ptr) == 1) {
            processEvent(event)
        }
    }

    private fun processEvent(sdlEvent: SDL_Event) {
        val event = when (sdlEvent.type) {
            SDL_QUIT,
            SDL_APP_TERMINATING, SDL_APP_LOWMEMORY, SDL_APP_DIDENTERBACKGROUND,
            SDL_APP_DIDENTERFOREGROUND, SDL_APP_WILLENTERBACKGROUND, SDL_APP_WILLENTERFOREGROUND -> {
                createAppEvent(sdlEvent, engine)
            }
            SDL_WINDOWEVENT -> {
                createWindowEvent(sdlEvent, engine)
            }
            SDL_KEYUP, SDL_KEYDOWN -> {
                createKeyEvent(sdlEvent, engine)
            }
            SDL_MOUSEBUTTONDOWN, SDL_MOUSEBUTTONUP, SDL_MOUSEMOTION, SDL_MOUSEWHEEL -> {
                createMouseEvent(sdlEvent, engine)
            }
            SDL_FINGERMOTION, SDL_FINGERDOWN, SDL_FINGERUP -> {
                // ignore event and don't log it
                null
            }
            else -> {
                val eventName = eventNames[sdlEvent.type]
                if (eventName == null)
                    engine.logger.event { "Unknown event: ${sdlEvent.type}" }
                else
                    engine.logger.event { eventName.toString() }
                null
            }
        }

        if (event != null) {
            engine.logger.event { event.toString() }
            all.raise(event)
        }

    }

    actual companion object {
        fun createKeyEvent(sdlEvent: SDL_Event, engine: Engine): EventKey {
            val timestamp = sdlEvent.common.timestamp.toULong()
            val keyEvent = sdlEvent.key
            val frame = engine.getFrame(keyEvent.windowID)
            return when (sdlEvent.type) {
                SDL_KEYDOWN -> EventKeyDown(
                    timestamp,
                    frame,
                    keyEvent.keysym.sym,
                    keyEvent.keysym.scancode,
                    keyEvent.repeat != 0.toUByte()
                )
                SDL_KEYUP -> EventKeyUp(timestamp, frame, keyEvent.keysym.sym, keyEvent.keysym.scancode)
                else -> throw EngineException("EventKey.createEvent was called with unknown type of SDL_Event")
            }
        }


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

        fun createMouseEvent(sdlEvent: SDL_Event, engine: Engine): EventMouse {
            val timestamp = sdlEvent.common.timestamp.toULong()
            return when (sdlEvent.type) {
                SDL_MOUSEBUTTONDOWN -> {
                    val buttonEvent = sdlEvent.button
                    EventMouseButtonDown(
                        timestamp,
                        engine.getFrame(buttonEvent.windowID),
                        mouseButtonFromValue(buttonEvent.button),
                        buttonEvent.x,
                        buttonEvent.y,
                        buttonEvent.clicks.toUInt()
                    )
                }
                SDL_MOUSEBUTTONUP -> {
                    val buttonEvent = sdlEvent.button
                    EventMouseButtonUp(
                        timestamp,
                        engine.getFrame(buttonEvent.windowID),
                        mouseButtonFromValue(buttonEvent.button),
                        buttonEvent.x,
                        buttonEvent.y,
                        buttonEvent.clicks.toUInt()
                    )
                }
                SDL_MOUSEMOTION -> {
                    val motionEvent = sdlEvent.motion
                    EventMouseMotion(
                        timestamp,
                        engine.getFrame(motionEvent.windowID),
                        motionEvent.x,
                        motionEvent.y,
                        motionEvent.xrel,
                        motionEvent.yrel
                    )
                }
                SDL_MOUSEWHEEL -> {
                    val wheelEvent = sdlEvent.wheel
                    EventMouseScroll(
                        timestamp,
                        engine.getFrame(wheelEvent.windowID),
                        wheelEvent.x,
                        wheelEvent.y
                    )
                }
                else -> throw EngineException("EventMouse.createEvent was called with unknown type of SDL_Event")
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

        actual val LogCategory = LoggerCategory("Events")

        val eventNames = mapOf(
            SDL_QUIT to "SDL_QUIT",
            SDL_APP_TERMINATING to "SDL_APP_TERMINATING",
            SDL_APP_LOWMEMORY to "SDL_APP_LOWMEMORY",
            SDL_APP_WILLENTERBACKGROUND to "SDL_APP_WILLENTERBACKGROUND",
            SDL_APP_DIDENTERBACKGROUND to "SDL_APP_DIDENTERBACKGROUND",
            SDL_APP_WILLENTERFOREGROUND to "SDL_APP_WILLENTERFOREGROUND",
            SDL_APP_DIDENTERFOREGROUND to "SDL_APP_DIDENTERFOREGROUND",
            SDL_WINDOWEVENT to "SDL_WINDOWEVENT",
            SDL_SYSWMEVENT to "SDL_SYSWMEVENT",
            SDL_KEYDOWN to "SDL_KEYDOWN",
            SDL_KEYUP to "SDL_KEYUP",
            SDL_TEXTEDITING to "SDL_TEXTEDITING",
            SDL_TEXTINPUT to "SDL_TEXTINPUT",
            SDL_KEYMAPCHANGED to "SDL_KEYMAPCHANGED",
            SDL_MOUSEMOTION to "SDL_MOUSEMOTION",
            SDL_MOUSEBUTTONDOWN to "SDL_MOUSEBUTTONDOWN",
            SDL_MOUSEBUTTONUP to "SDL_MOUSEBUTTONUP",
            SDL_MOUSEWHEEL to "SDL_MOUSEWHEEL",
            SDL_JOYAXISMOTION to "SDL_JOYAXISMOTION",
            SDL_JOYBALLMOTION to "SDL_JOYBALLMOTION",
            SDL_JOYHATMOTION to "SDL_JOYHATMOTION",
            SDL_JOYBUTTONDOWN to "SDL_JOYBUTTONDOWN",
            SDL_JOYBUTTONUP to "SDL_JOYBUTTONUP",
            SDL_JOYDEVICEADDED to "SDL_JOYDEVICEADDED",
            SDL_JOYDEVICEREMOVED to "SDL_JOYDEVICEREMOVED",
            SDL_CONTROLLERAXISMOTION to "SDL_CONTROLLERAXISMOTION",
            SDL_CONTROLLERBUTTONDOWN to "SDL_CONTROLLERBUTTONDOWN",
            SDL_CONTROLLERBUTTONUP to "SDL_CONTROLLERBUTTONUP",
            SDL_CONTROLLERDEVICEADDED to "SDL_CONTROLLERDEVICEADDED",
            SDL_CONTROLLERDEVICEREMOVED to "SDL_CONTROLLERDEVICEREMOVED",
            SDL_CONTROLLERDEVICEREMAPPED to "SDL_CONTROLLERDEVICEREMAPPED",
            SDL_FINGERDOWN to "SDL_FINGERDOWN",
            SDL_FINGERUP to "SDL_FINGERUP",
            SDL_FINGERMOTION to "SDL_FINGERMOTION",
            SDL_DOLLARGESTURE to "SDL_DOLLARGESTURE",
            SDL_DOLLARRECORD to "SDL_DOLLARRECORD",
            SDL_MULTIGESTURE to "SDL_MULTIGESTURE",
            SDL_CLIPBOARDUPDATE to "SDL_CLIPBOARDUPDATE",
            SDL_DROPFILE to "SDL_DROPFILE",
            SDL_DROPTEXT to "SDL_DROPTEXT",
            SDL_DROPBEGIN to "SDL_DROPBEGIN",
            SDL_DROPCOMPLETE to "SDL_DROPCOMPLETE",
            SDL_AUDIODEVICEADDED to "SDL_AUDIODEVICEADDED",
            SDL_AUDIODEVICEREMOVED to "SDL_AUDIODEVICEREMOVED",
            SDL_RENDER_TARGETS_RESET to "SDL_RENDER_TARGETS_RESET",
            SDL_RENDER_DEVICE_RESET to "SDL_RENDER_DEVICE_RESET",
            SDL_USEREVENT to "SDL_USEREVENT"
        )
    }
}


