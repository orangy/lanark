package ksdl

import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import sdl2.SDL_GetError

class KGraphicsException(message: String) : Exception(message)

fun getSDLErrorText() = SDL_GetError()!!.toKString()

@Suppress("NOTHING_TO_INLINE")
inline fun <T : CPointed> CPointer<T>?.checkSDLError(context: String = "SDL"): CPointer<T> {
    if (this == null) {
        val message = "$context Error: ${getSDLErrorText()}"
        throw KGraphicsException(message)
    }
    return this
}

@Suppress("NOTHING_TO_INLINE")
inline fun Int.checkSDLError(context: String = "SDL") {
    if (this != 0) {
        val message = "$context Error: ${getSDLErrorText()}"
        throw KGraphicsException(message)
    }
}