package ksdl

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import sdl2.SDL_Point
import sdl2.SDL_Rect

data class KSize(val width: Int, val height: Int)
data class KPoint(val x: Int, val y: Int)
data class KRect(val x: Int, val y: Int, val width: Int, val height: Int) {
    val origin get() = KPoint(x, y)
    val size get() = KSize(width, height)

    constructor(origin: KPoint, size: KSize) : this(origin.x, origin.y, size.width, size.height)
}

data class KMargins(val top: Int, val left: Int, val bottom: Int, val right: Int)

fun MemScope.SDL_Rect(value: KRect): CPointer<SDL_Rect> {
    val sdl = alloc<SDL_Rect>()
    sdl.x = value.x
    sdl.y = value.y
    sdl.w = value.width
    sdl.h = value.height
    return sdl.ptr
}

fun MemScope.SDL_Point(value: KPoint): CPointer<SDL_Point> {
    val sdl = alloc<SDL_Point>()
    sdl.x = value.x
    sdl.y = value.y
    return sdl.ptr
}
