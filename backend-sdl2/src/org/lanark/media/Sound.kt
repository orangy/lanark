package org.lanark.media

import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import org.lanark.io.*
import org.lanark.system.*
import sdl2.*

inline class Sound(private val soundPtr: CPointer<Mix_Chunk>) : Managed {

    override fun release() {
        Mix_FreeChunk(soundPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Sound ${soundPtr.rawValue}"

    companion object {
        fun load(path: String, fileSystem: FileSystem): Sound {
            return fileSystem.open(path).use { file ->
                val audio = Mix_LoadWAV_RW(file.handle, 0).checkSDLError("Mix_LoadWAV_RW")
                Sound(audio).also {
                    logger.system("Loaded $it from $path at $fileSystem")
                }
            }
        }
    }

}