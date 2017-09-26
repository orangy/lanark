package ksdl.system

import sdl2.*

fun Boolean.toSDLBoolean() = if (this) SDL_TRUE else SDL_FALSE