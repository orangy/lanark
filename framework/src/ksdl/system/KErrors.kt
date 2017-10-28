package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

class KPlatformException(message: String) : Exception(message)

fun getSDLErrorText() = SDL_GetError()!!.toKString()

@Suppress("NOTHING_TO_INLINE")
inline fun <T : CPointed> CPointer<T>?.checkSDLError(context: String = "SDL"): CPointer<T> {
    return this ?: throw KPlatformException("$context Error: ${getSDLErrorText()}")
}

@Suppress("NOTHING_TO_INLINE")
inline fun Int.checkSDLError(context: String = "SDL") {
    if (this != 0) {
        throw KPlatformException("$context Error: ${getSDLErrorText()}")
    }
}