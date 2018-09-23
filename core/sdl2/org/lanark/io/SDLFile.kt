package org.lanark.io

import sdl2.*
import kotlinx.cinterop.*
import org.lanark.system.*
import org.lanark.diagnostics.*

actual class File(val handle: CPointer<SDL_RWops>) : Managed {
    override fun release() {
        close()
    }

    actual val size: Long
        get() {
            val fn = handle.pointed.size.sdlError("File.size")
            return fn(handle)
        }

    actual val position: Long get() = seek(0, SeekFrom.Current)

    actual fun seek(position: Long, seekFrom: SeekFrom): Long {
        val fn = handle.pointed.seek.sdlError("File.size")
        return fn(handle, position, seekFrom.value)
    }

    actual fun close() {
        val fn = handle.pointed.close.sdlError("File.size")
        fn(handle)
    }
}

actual enum class SeekFrom(val value: Int) {
    Start(RW_SEEK_SET),
    Current(RW_SEEK_CUR),
    End(RW_SEEK_END),
}

