package org.lanark.application

import sdl2.*

actual enum class MessageBoxIcon(val flags: SDL_MessageBoxFlags) {
    Information(SDL_MESSAGEBOX_INFORMATION),
    Warning(SDL_MESSAGEBOX_WARNING),
    Error(SDL_MESSAGEBOX_ERROR)
}