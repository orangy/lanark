package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

data class KSize(val width: Int, val height: Int) {
    override fun toString() = "($width x $height)"
}

data class KPoint(val x: Int, val y: Int) {
    override fun toString() = "($x, $y)"
}

data class KRect(val x: Int, val y: Int, val width: Int, val height: Int) {
    val origin get() = KPoint(x, y)
    val size get() = KSize(width, height)

    constructor(origin: KPoint, size: KSize) : this(origin.x, origin.y, size.width, size.height)

    override fun toString() = "R($x, $y, $width, $height)"
    operator fun contains(point: KPoint): Boolean = memScoped {
        SDL_PointInRect(SDL_Point(point), SDL_Rect(this@KRect)).toBoolean()
    }

    fun intersects(other: KRect): Boolean = memScoped {
        SDL_HasIntersection(SDL_Rect(other), SDL_Rect(this@KRect)).toBoolean()
    }

    fun isEmpty(): Boolean = memScoped {
        SDL_RectEmpty(SDL_Rect(this@KRect)).toBoolean()
    }
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
