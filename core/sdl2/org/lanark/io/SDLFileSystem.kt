package org.lanark.io

import org.lanark.diagnostics.*
import sdl2.*

class SDLFileSystem : FileSystem {
    override fun open(path: String): File {
        val handle = SDL_RWFromFile(path, "rb").sdlError("SDL_RWFromFile $path")
        return File(handle)
    }

    override fun toString() = "FileSystem(Default)"
}

