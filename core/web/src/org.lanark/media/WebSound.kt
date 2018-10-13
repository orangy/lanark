package org.lanark.media

import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*

actual class Sound : Managed {
    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual fun ResourceContext.loadSound(path: String, fileSystem: FileSystem): Sound {
    return Sound()
}
