package org.lanark.geometry

import kotlinx.cinterop.*
import sdl2.*

fun MemScope.SDL_Rect(value: Rect): CPointer<SDL_Rect> {
    val sdl = alloc<SDL_Rect>()
    sdl.x = value.x
    sdl.y = value.y
    sdl.w = value.width
    sdl.h = value.height
    return sdl.ptr
}

fun MemScope.SDL_Rect(x: Int, y: Int, width: Int, height: Int): CPointer<SDL_Rect> {
    val sdl = alloc<SDL_Rect>()
    sdl.x = x
    sdl.y = y
    sdl.w = width
    sdl.h = height
    return sdl.ptr
}

fun MemScope.SDL_Rect(position: Point, size: Size): CPointer<SDL_Rect> {
    val sdl = alloc<SDL_Rect>()
    sdl.x = position.x
    sdl.y = position.y
    sdl.w = size.width
    sdl.h = size.height
    return sdl.ptr
}

fun MemScope.SDL_Point(value: Point): CPointer<SDL_Point> {
    val sdl = alloc<SDL_Point>()
    sdl.x = value.x
    sdl.y = value.y
    return sdl.ptr
}
