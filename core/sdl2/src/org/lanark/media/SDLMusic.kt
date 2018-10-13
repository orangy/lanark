package org.lanark.media

import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*
import sdl2.*

actual class Music(private val logger: Logger, private val musicPtr: CPointer<Mix_Music>) : Managed {
    override fun release() {
        Mix_FreeMusic(musicPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Music ${musicPtr.rawValue}"

    actual fun play(repeat: Int?) {
        logger.system("Playing $this")
        Mix_PlayMusic(musicPtr, repeat ?: -1)
    }

    actual fun stop() {
        Mix_HaltMusic() // TODO: figure out channels
        logger.system("Stopped $this")
    }
}

actual fun ResourceContext.loadMusic(path: String, fileSystem: FileSystem): Music {
    return fileSystem.open(path, FileOpenMode.Read).use { file ->
        val audio = Mix_LoadMUS_RW(file.handle, 0).sdlError("Mix_LoadMUS_RW")
        Music(logger, audio).also {
            logger.system("Loaded $it from $path at $fileSystem")
        }
    }
}
