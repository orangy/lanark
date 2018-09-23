package org.lanark.io

interface FileSystem {
    fun open(path: String): File
}

