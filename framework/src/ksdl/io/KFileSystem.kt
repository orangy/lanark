package ksdl.io

interface KFileSystem {
    fun open(path: String): KFile

    companion object {
        val Default = KFileSystemDefault()
    }
}

