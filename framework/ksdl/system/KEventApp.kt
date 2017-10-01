package ksdl.system

import sdl2.*

abstract class KEventApp(sdlEventType: SDL_EventType) : KEvent(sdlEventType) {
    companion object {
        fun createEvent(sdlEvent: SDL_Event): KEventApp {
            val type = sdlEvent.type
            return when (type) {
                SDL_APP_TERMINATING -> KEventAppTerminating(type)
                SDL_APP_LOWMEMORY -> KEventAppLowMemory(type)
                SDL_APP_DIDENTERBACKGROUND -> KEventAppEnteredBackground(type)
                SDL_APP_DIDENTERFOREGROUND -> KEventAppEnteredForeground(type)
                SDL_APP_WILLENTERBACKGROUND -> KEventAppEnteringBackground(type)
                SDL_APP_WILLENTERFOREGROUND -> KEventAppEnteringForeground(type)
                else -> throw KPlatformException("KEventApp.createEvent was called with wrong type of SDL_Event")
            }
        }
    }
}

class KEventAppTerminating(sdlEvent: SDL_EventType) : KEventApp(sdlEvent)
class KEventAppLowMemory(sdlEvent: SDL_EventType) : KEventApp(sdlEvent)
class KEventAppEnteredBackground(sdlEvent: SDL_EventType) : KEventApp(sdlEvent)
class KEventAppEnteredForeground(sdlEvent: SDL_EventType) : KEventApp(sdlEvent)
class KEventAppEnteringBackground(sdlEvent: SDL_EventType) : KEventApp(sdlEvent)
class KEventAppEnteringForeground(sdlEvent: SDL_EventType) : KEventApp(sdlEvent)
