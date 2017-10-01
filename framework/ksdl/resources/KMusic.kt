package ksdl.resources

import kotlinx.cinterop.*
import ksdl.system.*
import sdl2.*

class KMusic(val musicPtr: CPointer<Mix_Music>) {
    init {
        logger.system("Created $this")
    }

    fun release() {
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
}