package org.lanark.media

import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*

expect class Video : Managed {
    
}

expect fun ResourceContext.loadVideo(path: String, fileSystem: FileSystem): Video