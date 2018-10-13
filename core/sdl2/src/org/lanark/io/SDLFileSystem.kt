package org.lanark.io

import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import platform.posix.*
import sdl2.*

class SDLFileSystem : FileSystem {
    override fun delete(path: String) {
        remove(path)
    }

    override fun open(path: String, mode: FileOpenMode): File {
        val handle = SDL_RWFromFile(path, mode.value).sdlError("SDL_RWFromFile $path")
        return File(handle)
    }

    override fun currentDirectory() = memScoped {
        val buffer = allocArray<ByteVar>(1024)
        getcwd(buffer, 1024)
        buffer.toKString()
    }

    override fun toString() = "FileSystem(SDL2)"
}

