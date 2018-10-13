package org.lanark.media

import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import sdl2.*

actual class Sound(val engine: Engine, private val soundPtr: CPointer<Mix_Chunk>) : Managed {
    override fun release() {
        Mix_FreeChunk(soundPtr)
        engine.logger.system("Released $this")
    }

    override fun toString() = "Sound ${soundPtr.rawValue}"
}