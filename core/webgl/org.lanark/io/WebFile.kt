package org.lanark.io

import org.lanark.system.*

actual class File : Managed {
    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual val size: Long
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val position: Long
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    actual fun read(count: Int): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun write(source: ByteArray): ULong {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun seek(position: Long, seekFrom: SeekFrom): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun close() {}
}

actual enum class FileOpenMode {
    Read,
    Truncate,
    Append,
    Update,
}