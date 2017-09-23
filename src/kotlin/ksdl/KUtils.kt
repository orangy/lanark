package ksdl

import sdl2.SDL_FALSE
import sdl2.SDL_TRUE

fun Boolean.toSDLBoolean() = if (this) SDL_TRUE else SDL_FALSE