package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

class KPlatformException(message: String) : Exception(message)

fun getSDLErrorText() = SDL_GetError()!!.toKString()

@Suppress("NOTHING_TO_INLINE")
inline fun <T : CPointed> CPointer<T>?.checkSDLError(context: String = "SDL"): CPointer<T> {
    if (this == null) {
        val message = "$context Error: ${getSDLErrorText()}"
        throw KPlatformException(message)
    }
    return this
}

@Suppress("NOTHING_TO_INLINE")
inline fun Int.checkSDLError(context: String = "SDL") {
    if (this != 0) {
        val message = "$context Error: ${getSDLErrorText()}"
        throw KPlatformException(message)
    }
}