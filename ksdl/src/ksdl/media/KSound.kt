package ksdl.media

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.io.*
import ksdl.system.*
import sdl2.*

class KSound(val soundPtr: CPointer<Mix_Chunk>) : KManaged {

    override fun release() {
        Mix_FreeChunk(soundPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Sound ${soundPtr.rawValue}"

    companion object {
        fun load(path: String, fileSystem: KFileSystem): KSound {
            return fileSystem.open(path).use { file ->
                val audio = Mix_LoadWAV_RW(file.handle, 0).checkSDLError("Mix_LoadWAV_RW")
                KSound(audio).also {
                    logger.system("Loaded $it from $path at $fileSystem")
                }
            }
        }
    }

}