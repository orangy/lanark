package org.lanark.io

import org.lanark.system.*

expect class File : Managed {
    val size: ULong
    val position: ULong
    fun read(count: Int): ByteArray 
    fun write(source: ByteArray): ULong
    fun seek(position: Long, seekFrom: SeekFrom = SeekFrom.Start): ULong
    fun close()
}

expect enum class FileOpenMode {
    Read,
    Truncate,
    Append,
    Update, 
}