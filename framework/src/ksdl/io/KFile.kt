package ksdl.io

import sdl2.*
import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.system.*

class KFile(val handle: CPointer<SDL_RWops>) : KManaged {
    override fun release() {
        close()
    }

    val size: Long
        get() {
            val fn = handle.pointed.size.checkSDLError("File.size")
            return fn(handle)
        }

    val position: Long get() = seek(0, SeekFrom.Current)

    fun seek(position: Long, seekFrom: SeekFrom = SeekFrom.Start): Long {
        val fn = handle.pointed.seek.checkSDLError("File.size")
        return fn(handle, position, seekFrom.value)
    }

    fun close() {
        val fn = handle.pointed.close.checkSDLError("File.size")
        fn(handle)
    }
}

enum class SeekFrom(val value: Int) {
    Start(RW_SEEK_SET),
    Current(RW_SEEK_CUR),
    End(RW_SEEK_END),
}

