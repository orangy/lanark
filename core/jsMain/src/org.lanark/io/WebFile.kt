package org.lanark.io

import kotlinx.io.core.*
import org.lanark.system.*

actual class File(val path: String) : Managed {
    override fun release() {
    }

    actual val size: ULong
        get() = 0u

    actual val position: ULong
        get() = 0u

    actual fun read(count: Int): ByteArray {
        return ByteArray(0)
    }

    actual fun write(source: ByteArray): ULong {
        return 0u
    }

    actual fun seek(position: Long, seekFrom: SeekFrom): ULong {
        return 0u
    }

    actual fun close() {}
    
    actual fun input(): Input {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun output(): Output {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual enum class FileOpenMode {
    Read,
    Truncate,
    Append,
    Update,
}