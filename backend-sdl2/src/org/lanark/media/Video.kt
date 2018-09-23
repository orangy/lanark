package org.lanark.media

import org.lanark.diagnostics.*
import org.lanark.io.*
import org.lanark.system.*

class Video : Managed {
    override fun release() {}

    companion object {
        fun load(path: String, fileSystem: FileSystem): Video {
            return fileSystem.open(path).use {
                Video().also {
                    logger.system("Loaded $it from $path at $fileSystem")
                }
            }
        }
    }

}