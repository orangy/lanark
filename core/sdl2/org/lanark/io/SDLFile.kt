package org.lanark.io

import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import sdl2.*

actual class File(val handle: CPointer<SDL_RWops>) : Managed {
    override fun release() {
        close()
    }

    actual val size: ULong
        get() {
            val fn = handle.pointed.size.sdlError("File.size")
            return fn(handle).toULong()
        }

    actual val position: ULong get() = seek(0u, SeekFrom.Current)

    actual fun seek(position: ULong, seekFrom: SeekFrom): ULong {
        val fn = handle.pointed.seek.sdlError("File.seek")
        return fn(handle, position.toLong(), seekFrom.value).toULong()
    }

    actual fun write(source: ByteArray): ULong = memScoped {
        val fn = handle.pointed.write.sdlError("File.write")
        return fn(handle, source.toCValues().ptr, 1u, source.size.toULong())
    }

    actual fun read(count: Int): ByteArray = memScoped {
        val fn = handle.pointed.read.sdlError("File.read")
        val buffer = allocArray<ByteVar>(count)
        val numBytes = fn(handle, buffer, 1u, count.toULong())
        return buffer.readBytes(numBytes.toInt())
    }

    actual fun close() {
        val fn = handle.pointed.close.sdlError("File.close")
        fn(handle)
    }
}

actual enum class SeekFrom(val value: Int) {
    Start(RW_SEEK_SET),
    Current(RW_SEEK_CUR),
    End(RW_SEEK_END),
}

actual enum class FileOpenMode(val value: String) {
    Read("rb"),
    Truncate("wb+"),
    Append("ab"),
    Update("rb+"),
}