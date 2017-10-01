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

    fun play() {
        Mix_PlayMusic(musicPtr, -1)
    }

    fun stop() {
        Mix_HaltMusic() // TODO: figure out channels
    }
}