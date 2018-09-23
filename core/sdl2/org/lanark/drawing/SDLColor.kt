package org.lanark.drawing

import kotlinx.cinterop.*
import sdl2.*

fun Color.toRawColor(format: CPointer<SDL_PixelFormat>?): UInt {
    return SDL_MapRGBA(format, red, green, blue, alpha)
}
