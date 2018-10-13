package org.lanark.io

import java.nio.file.*

actual object FileSystems {
    actual val Default: FileSystem = LWFileSystem()
}

class LWFileSystem : FileSystem {
    override fun delete(path: String) {
        Files.delete(Paths.get(path))
    }

    override fun open(path: String, mode: FileOpenMode): File {
        return File(Files.newByteChannel(Paths.get(path), *mode.value))
    }

    override fun currentDirectory() : String = java.io.File(".").absolutePath.toString()

    override fun toString() = "FileSystem(LWJGL)"
}

actual enum class SeekFrom {
    Start,
    Current,
    End,
}