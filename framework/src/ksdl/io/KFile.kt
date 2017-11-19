package ksdl.io

import sdl2.*
import kotlinx.cinterop.*
import ksdl.system.*

class KFile(val handle: CPointer<SDL_RWops>) : KManaged {
    override fun release() {
        SDL_FreeRW(handle)
    }
}

