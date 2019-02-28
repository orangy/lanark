package org.lanark.media

import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*

actual class Music : Managed {
    override fun release() {
    }

    actual fun play(repeat: Int?) {}
    actual fun stop() {}
}

actual fun ResourceContext.loadMusic(path: String, fileSystem: FileSystem): Music {
    return Music()
}

