package ksdl.io

import ksdl.diagnostics.*

class KFileSystemMounted : KFileSystem {
    override fun open(path: String): KFile {
        val mount = mounts.entries.firstOrNull { path.startsWith(it.key) }
                ?: throw KPlatformException("Path $path is not found")
        return mount.value.open(path.removePrefix(mount.key))
    }

    private val mounts = mutableMapOf<String, KFileSystem>()

    fun mount(path: String, fileSystem: KFileSystem) {
        mounts.put(path, fileSystem)
    }

    override fun toString() = "FileSystem(Mounted)"
}