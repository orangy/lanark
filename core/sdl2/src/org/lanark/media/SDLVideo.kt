package org.lanark.media

import org.lanark.diagnostics.*
import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*

actual class Video(val logger: Logger) : Managed {
    override fun release() {
        logger.system("Released $this")
    }
}

actual fun ResourceContext.loadVideo(path: String, fileSystem: FileSystem): Video {
    return fileSystem.open(path, FileOpenMode.Read).use {
        Video(logger).also {
            logger.system("Loaded $it from $path at $fileSystem")
        }
    }
}
