package org.lanark.io

actual object FileSystems {
    actual val Default: FileSystem = WebFileSystem()
        
}

class WebFileSystem : FileSystem {
    override fun open(path: String, mode: FileOpenMode): File {
        return File(path)
    }

    override fun currentDirectory(): String {
        return ""
    }

    override fun delete(path: String) {
    }

}

actual enum class SeekFrom {
    Start,
    Current,
    End,
}