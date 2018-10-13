package org.lanark.media

import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*

expect class Sound : Managed {
}

expect fun ResourceContext.loadSound(path: String, fileSystem: FileSystem): Sound 