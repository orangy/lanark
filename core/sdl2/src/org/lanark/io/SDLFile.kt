package org.lanark.io

import kotlinx.cinterop.*
import kotlinx.io.core.*
import kotlinx.io.pool.*
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

    actual val position: ULong get() = seek(0, SeekFrom.Current)

    actual fun seek(position: Long, seekFrom: SeekFrom): ULong {
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

    @ExperimentalIoApi
    actual fun input(): Input {
        return FileInput(this)
    }

    actual fun output(): Output {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

@ExperimentalIoApi
class FileInput(private val file: File, pool: ObjectPool<IoBuffer> = IoBuffer.Pool) : AbstractInput(pool = pool) {
    override fun fill(): IoBuffer? {
        val buffer: IoBuffer = pool.borrow()
        try {
            val bytes = file.read(1024)
            if (bytes.isEmpty()) {
                buffer.release(pool)
                return null
            }
            buffer.writeFully(bytes)
            return buffer
        } catch (t: Throwable) {
            buffer.release(pool)
            throw t
        }
    }

    override fun closeSource() {
        file.close()
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