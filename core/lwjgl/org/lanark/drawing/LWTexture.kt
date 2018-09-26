package org.lanark.drawing

import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.system.*
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.*

actual class Texture(val textureHandle: Int) : Managed {
    override fun release() {
        
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
    val texture = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, texture)
    MemoryStack.stackPush().use { stack ->
        val w = stack.mallocInt(1)
        val h = stack.mallocInt(1)
        val comp = stack.mallocInt(1)
        stbi_set_flip_vertically_on_load(true)
        val image = stbi_load(path, w, h, comp, 4) ?: throw RuntimeException(
            "Failed to load a texture file!" + System.lineSeparator() + stbi_failure_reason()
        )

        val width = w.get()
        val height = h.get()
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        stbi_image_free(image)
    }
    return Texture(texture)
}

actual fun Renderer.draw(tile: Tile, position: Point) {}
actual fun Renderer.fill(tile: Tile, destinationRect: Rect) {}