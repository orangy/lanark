package org.lanark.io

class FileSystemMounted : FileSystem {
    override fun currentDirectory(): String = throw UnsupportedOperationException("Mounted file system does not support current directory")

    override fun open(path: String, mode: FileOpenMode): File {
        val mount = mounts.entries.firstOrNull { path.startsWith(it.key) }
            ?: throw PathNotFoundException("Path $path is not found")
        return mount.value.open(path.removePrefix(mount.key), mode)
    }

    private val mounts = mutableMapOf<String, FileSystem>()

    fun mount(path: String, fileSystem: FileSystem) {
        mounts.put(path, fileSystem)
    }

    override fun toString() = "FileSystem(Mounted)"
}