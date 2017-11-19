package ksdl.resources

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.io.*
import ksdl.system.*
import sdl2.*

class KMusic(val musicPtr: CPointer<Mix_Music>) : KManaged {
    init {
        logger.system("Created $this")
    }

    override fun release() {
        Mix_FreeMusic(musicPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Music ${musicPtr.rawValue}"

    fun play(repeat: Int = -1) {
        Mix_PlayMusic(musicPtr, repeat)
    }

    fun stop() {
        Mix_HaltMusic() // TODO: figure out channels
    }

    companion object {
        fun load(path: String, fileSystem: KFileSystem): KMusic {
            val file = fileSystem.open(path)
            val audio = Mix_LoadMUS_RW(file.handle, 0).checkSDLError("Mix_LoadMUS_RW")
            return KMusic(audio).also {
                logger.system("Loaded $it from $path at $fileSystem")
            }
        }
    }
}