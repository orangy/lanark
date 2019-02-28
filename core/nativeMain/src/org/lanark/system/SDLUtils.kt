package org.lanark.system

import sdl2.*

fun Boolean.toSDLBoolean() = if (this) SDL_TRUE else SDL_FALSE
fun SDL_bool.toBoolean() = this == SDL_TRUE