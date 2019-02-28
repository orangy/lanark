package org.lanark.io

actual object FileSystems {
    actual val Default: FileSystem = SDLFileSystem()
}