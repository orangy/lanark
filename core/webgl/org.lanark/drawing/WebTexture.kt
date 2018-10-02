package org.lanark.drawing

import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.system.*

actual class Texture : Managed {
    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual val size: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun Frame.loadTexture(path: String, fileSystem: FileSystem): Texture {
    TODO()
}

actual fun Frame.draw(texture: Texture) {
}

actual fun Frame.draw(texture: Texture, sourceRect: Rect, destinationRect: Rect) {
}

actual fun Frame.draw(texture: Texture, destinationRect: Rect) {
}

actual fun Frame.fill(texture: Texture, destinationRect: Rect) {
}

actual fun Frame.draw(texture: Texture, position: Point) {
    draw(texture, Rect(position, texture.size))
}

actual fun Frame.draw(texture: Texture, position: Point, size: Size) {
    draw(texture, Rect(position, size))
}
