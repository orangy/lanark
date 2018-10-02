package org.lanark.io

actual object FileSystems {
    actual val Default: FileSystem
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual enum class SeekFrom {
    Start,
    Current,
    End,
}