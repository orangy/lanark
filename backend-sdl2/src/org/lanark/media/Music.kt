package org.lanark.media

import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import org.lanark.io.*
import org.lanark.system.*
import sdl2.*

inline class Music(private val musicPtr: CPointer<Mix_Music>) : Managed {
    override fun release() {
        Mix_FreeMusic(musicPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Music ${musicPtr.rawValue}"

    fun play(repeat: Int = -1) {
        logger.system("Playing $this")
        Mix_PlayMusic(musicPtr, repeat)
    }

    fun stop() {
        Mix_HaltMusic() // TODO: figure out channels
        logger.system("Stopped $this")
    }

    companion object {
        fun load(path: String, fileSystem: FileSystem): Music {
            return fileSystem.open(path).use { file ->
                val audio = Mix_LoadMUS_RW(file.handle, 0).checkSDLError("Mix_LoadMUS_RW")
                Music(audio).also {
                    logger.system("Loaded $it from $path at $fileSystem")
                }
            }
        }
    }
}