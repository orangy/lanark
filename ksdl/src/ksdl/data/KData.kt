package ksdl.data

import ksdl.diagnostics.*
import ksdl.io.*
import ksdl.system.*

class KData : KManaged {
    override fun release() {}

    companion object {
        fun load(path: String, fileSystem: KFileSystem): KData {
            return fileSystem.open(path).use { file ->
                KData().also {
                    logger.system("Loaded $it from $path at $fileSystem")
                }
            }
        }
    }

    override fun toString() = "Data"
}