package org.lanark.media

import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import sdl2.*

actual class Music(val engine: Engine, private val musicPtr: CPointer<Mix_Music>) : Managed {
    override fun release() {
        Mix_FreeMusic(musicPtr)
        engine.logger.system("Released $this")
    }

    override fun toString() = "Music ${musicPtr.rawValue}"

    actual fun play(repeat: Int?) {
        engine.logger.system("Playing $this")
        Mix_PlayMusic(musicPtr, repeat ?: -1)
    }

    actual fun stop() {
        Mix_HaltMusic() // TODO: figure out channels
        engine.logger.system("Stopped $this")
    }
}