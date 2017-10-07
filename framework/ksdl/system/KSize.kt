package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

data class KSize(val width: Int, val height: Int) {
    operator fun plus(other: KSize) = KSize(width + other.width, height + other.height)
    operator fun minus(other: KSize) = KSize(width - other.width, height - other.height)
    override fun toString() = "{$width x $height}"
    operator fun times(scale: Double) = KSize((width * scale).toInt(), (height * scale).toInt())
}

data class KPoint(val x: Int, val y: Int) {
    operator fun plus(other: KVector) = KPoint(x + other.x, y + other.y)
    operator fun minus(other: KVector) = KVector(x - other.x, y - other.y)
    override fun toString() = "[$x, $y]"
}

data class KVector(val x: Int, val y: Int) {
    operator fun plus(other: KVector) = KPoint(x + other.x, y + other.y)
    operator fun minus(other: KVector) = KPoint(x - other.x, y - other.y)
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
