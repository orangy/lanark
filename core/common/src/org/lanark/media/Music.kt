package org.lanark.media

import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*

expect class Music : Managed {
    fun play(repeat: Int? = null)
    fun stop()
}

expect fun ResourceContext.loadMusic(path: String, fileSystem: FileSystem): Music