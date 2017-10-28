package ksdl.geometry

import kotlinx.cinterop.*
import ksdl.system.*
import sdl2.*

data class KRect(val x: Int, val y: Int, val width: Int, val height: Int) {
    val origin get() = KPoint(x, y)
    val size get() = KSize(width, height)

    constructor(origin: KPoint, size: KSize) : this(origin.x, origin.y, size.width, size.height)

    override fun toString() = "R($x, $y, $width, $height)"
    operator fun contains(point: KPoint): Boolean {
        return point.x >= x && point.x < x + width && point.y >= y && point.y < y + height
    }

    fun intersects(other: KRect): Boolean = memScoped {
        SDL_HasIntersection(SDL_Rect(other), SDL_Rect(this@KRect)).toBoolean()
    }

    fun isEmpty(): Boolean = memScoped {
        SDL_RectEmpty(SDL_Rect(this@KRect)).toBoolean()
    }
}

fun MemScope.SDL_Rect(value: KRect): CPointer<SDL_Rect> {
    val sdl = alloc<SDL_Rect>()
    sdl.x = value.x
    sdl.y = value.y
    sdl.w = value.width
    sdl.h = value.height
    return sdl.ptr
}

fun MemScope.SDL_Rect(position: KPoint, size: KSize): CPointer<SDL_Rect> {
    val sdl = alloc<SDL_Rect>()
    sdl.x = position.x
    sdl.y = position.y
    sdl.w = size.width
    sdl.h = size.height
    return sdl.ptr
}

fun MemScope.SDL_Point(value: KPoint): CPointer<SDL_Point> {
    val sdl = alloc<SDL_Point>()
    sdl.x = value.x
    sdl.y = value.y
    return sdl.ptr
}
