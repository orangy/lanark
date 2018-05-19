package ksdl.media

import ksdl.diagnostics.*
import ksdl.io.*
import ksdl.system.*

class KVideo : KManaged {
    override fun release() {}

    companion object {
        fun load(path: String, fileSystem: KFileSystem): KVideo {
            return fileSystem.open(path).use { file ->
                KVideo().also {
                    logger.system("Loaded $it from $path at $fileSystem")
                }
            }
        }
    }

}