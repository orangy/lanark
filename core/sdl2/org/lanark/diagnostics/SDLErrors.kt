package org.lanark.diagnostics

import kotlinx.cinterop.*
import sdl2.*

class PlatformException(message: String) : Exception(message)

fun getSDLErrorText() = SDL_GetError()!!.toKString()

@Suppress("NOTHING_TO_INLINE")
inline fun <T : CPointed> CPointer<T>?.sdlError(context: String = "SDL"): CPointer<T> {
    return this ?: throw PlatformException("$context Error: ${getSDLErrorText()}")
}

@Suppress("NOTHING_TO_INLINE")
inline fun Int.sdlError(context: String = "SDL") {
    if (this != 0) {
        throw PlatformException("$context Error: ${getSDLErrorText()}")
    }
}