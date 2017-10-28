package ksdl.io

import sdl2.*
import kotlinx.cinterop.*

class KFile(val handle: CPointer<SDL_RWops>) {
    fun close() {
        SDL_FreeRW(handle)
    }
}