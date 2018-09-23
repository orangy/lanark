package org.lanark.io

import org.lanark.system.*

expect class File : Managed {
    val size: Long
    val position: Long
    fun seek(position: Long, seekFrom: SeekFrom = SeekFrom.Start) : Long
    fun close()
}

