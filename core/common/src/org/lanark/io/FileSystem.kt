package org.lanark.io

interface FileSystem {
    fun open(path: String, mode: FileOpenMode): File
    fun currentDirectory(): String
    fun delete(path: String)
    fun combine(path: String, relativePath: String): String 
    fun sibling(path: String, relativePath: String): String 
}

