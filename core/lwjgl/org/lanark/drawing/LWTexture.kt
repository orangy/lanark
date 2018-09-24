package org.lanark.drawing

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

actual fun Renderer.draw(texture: Texture) {}
actual fun Renderer.draw(texture: Texture, sourceRect: Rect, destinationRect: Rect) {}
actual fun Renderer.draw(texture: Texture, destinationRect: Rect) {}
actual fun Renderer.fill(texture: Texture, destinationRect: Rect) {}
actual fun Renderer.draw(texture: Texture, position: Point) {}
actual fun Renderer.draw(texture: Texture, position: Point, size: Size) {}
actual fun Renderer.loadTexture(path: String, fileSystem: FileSystem): Texture {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun Renderer.draw(tile: Tile, position: Point) {}
actual fun Renderer.fill(tile: Tile, destinationRect: Rect) {}