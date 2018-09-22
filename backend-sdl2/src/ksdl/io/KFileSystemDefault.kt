package ksdl.io

import ksdl.diagnostics.*
import sdl2.*

class KFileSystemDefault : KFileSystem {
    override fun open(path: String): KFile {
        val handle = SDL_RWFromFile(path, "rb").checkSDLError("SDL_RWFromFile $path")
        return KFile(handle)
    }

    override fun toString() = "FileSystem(Default)"
}