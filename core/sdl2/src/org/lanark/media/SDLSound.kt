package org.lanark.media

import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*
import sdl2.*

actual class Sound(val logger: Logger, private val soundPtr: CPointer<Mix_Chunk>) : Managed {
    override fun release() {
        Mix_FreeChunk(soundPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Sound ${soundPtr.rawValue}"
}


actual fun ResourceContext.loadSound(path: String, fileSystem: FileSystem): Sound {
    return fileSystem.open(path, FileOpenMode.Read).use { file ->
        val audio = Mix_LoadWAV_RW(file.handle, 0).sdlError("Mix_LoadWAV_RW")
        Sound(logger, audio).also {
            logger.system("Loaded $it from $path at $fileSystem")
        }
    }
}