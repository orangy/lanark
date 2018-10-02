package org.lanark.drawing

import org.khronos.webgl.*
import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.system.*

typealias gl = WebGLRenderingContext

actual class Texture(texture: WebGLTexture) : Managed {
    override fun release() {
    }

    actual val size: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun Frame.loadTexture(path: String, fileSystem: FileSystem): Texture {
    val texture = context.createTexture() ?: throw EngineException("Couldn't create texture for $path")
    context.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
    val pixel = Uint8Array(4).also {
        it[0] = 0
        it[1] = 0
        it[2] = 255.toByte()
        it[3] = 255.toByte()
    }
    context.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, 1, 1, 0, gl.RGBA, gl.UNSIGNED_BYTE, pixel)

    // LOAD TEXTURE
    
    return Texture(texture)
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
