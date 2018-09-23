package org.lanark.system

import sdl2.*

actual object Math {
    actual val pi: Double = 3.1415926535897932

    actual fun abs(value: Double) = if (value < 0) -value else value
    actual fun abs(value: Int) = if (value < 0) -value else value

    actual fun pow(value: Int, power: Int): Int {
        var result = value
        for (i in 2..power)
            result *= value
        return result
    }

    actual fun floor(value: Double): Double = SDL_floor(value)

    actual fun round(value: Double): Double = platform.posix.round(value)
    
    actual fun round(value: Double, decimals: Int): Double {
        val multiplier = pow(10, decimals)
        return round(value * multiplier) / multiplier
    }

    actual fun sqrt(value: Double): Double = SDL_sqrt(value)
    actual fun cos(value: Double): Double = SDL_cos(value)
    actual fun sin(value: Double): Double = SDL_sin(value)
}