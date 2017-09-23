package ksdl

import kotlinx.cinterop.CValuesRef
import sdl2.SDL_FreeSurface
import sdl2.SDL_Surface

class KSurface(val surfacePtr: CValuesRef<SDL_Surface>) {



    fun destroy() {
        SDL_FreeSurface(surfacePtr)
    }
}

