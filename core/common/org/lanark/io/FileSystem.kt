package org.lanark.io

interface FileSystem {
    fun open(path: String, mode: FileOpenMode): File
    fun currentDirectory(): String
}

