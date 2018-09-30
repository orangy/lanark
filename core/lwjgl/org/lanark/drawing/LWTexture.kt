package org.lanark.drawing

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.system.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.*

actual class Texture(val engine: Engine, val textureId: Int, actual val size: Size) : Managed {
    override fun release() {
        glDeleteTextures(textureId)
        engine.logger.system("Released $this")
    }

    override fun toString() = "Texture $textureId"
}

actual fun Frame.loadTexture(path: String, fileSystem: FileSystem): Texture {
    val texture = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, texture)
    glEnable(GL_TEXTURE_2D)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    MemoryStack.stackPush().use { stack ->
        val w = stack.mallocInt(1)
        val h = stack.mallocInt(1)
        val comp = stack.mallocInt(1)
        stbi_set_flip_vertically_on_load(true)
        val image = stbi_load(path, w, h, comp, 4)
            ?: throw EngineException("Failed to load a texture file: ${stbi_failure_reason()}")

        val width = w.get()
        val height = h.get()
        /*
            glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
            glTexSubImage2D(GL_TEXTURE_2D, 0​, 0, 0, width​, height​, GL_BGRA, GL_UNSIGNED_BYTE, pixels);
         */
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        stbi_image_free(image)
        return Texture(engine, texture, Size(width, height)).also {
            engine.logger.system("Loaded $it from $path at $fileSystem")
        } 
        
    }
}

actual fun Frame.draw(texture: Texture) {
    prepareContextFor2D(texture)
    drawStrip(0f, 1f, 1f, 0f, Rect(0, 0, texture.width, texture.height))
}

actual fun Frame.draw(texture: Texture, sourceRect: Rect, destinationRect: Rect) {
    val minU = sourceRect.left.toFloat() / texture.width
    val maxU = sourceRect.right.toFloat() / texture.width
    val minV = 1.0f - sourceRect.top.toFloat() / texture.height // 1.0f - is because texture is upside down
    val maxV = 1.0f - sourceRect.bottom.toFloat() / texture.height
    prepareContextFor2D(texture)
    drawStrip(minU, minV, maxU, maxV, destinationRect)
}


actual fun Frame.draw(texture: Texture, destinationRect: Rect) {
    prepareContextFor2D(texture)
    drawStrip(0f, 1f, 1f, 0f, destinationRect)
}

actual fun Frame.fill(texture: Texture, destinationRect: Rect) {
    prepareContextFor2D(texture)
    drawStrip(0f, 1f, 1f, 0f, destinationRect)
}

actual fun Frame.draw(texture: Texture, position: Point) {
    draw(texture, Rect(position, texture.size))
}

actual fun Frame.draw(texture: Texture, position: Point, size: Size) {
    draw(texture, Rect(position, size))
}

private fun Frame.drawStrip(minU: Float, minV: Float, maxU: Float, maxV: Float, destinationRect: Rect) {
    val logicalSize = size
    draw(GL_TRIANGLE_STRIP) {
        glTexCoord2f(minU, minV)
        glVertex2f(destinationRect.left.toFloat(), logicalSize.height - destinationRect.top.toFloat()) // Upper left

        glTexCoord2f(minU, maxV)
        glVertex2f(destinationRect.left.toFloat(), logicalSize.height - destinationRect.bottom.toFloat()) // Lower left

        glTexCoord2f(maxU, minV)
        glVertex2f(destinationRect.right.toFloat(), logicalSize.height - destinationRect.top.toFloat()) // Upper right

        glTexCoord2f(maxU, maxV)
        glVertex2f(destinationRect.right.toFloat(), logicalSize.height - destinationRect.bottom.toFloat()) // Lower right
    }
}

private inline fun Frame.prepareContextFor2D(texture: Texture) {
    val logicalSize = size
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0.0, logicalSize.width.toDouble(), 0.0, logicalSize.height.toDouble(), 0.0, 1.0)
    glMatrixMode(GL_MODELVIEW)
    glColor4f(1f, 1f, 1f, 1f)
    glEnable(GL_BLEND)
    glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
    glBlendEquation(GL_FUNC_ADD)
    glBindTexture(GL_TEXTURE_2D, texture.textureId)
}

private inline fun Frame.draw(mode: Int, body: () -> Unit) {
    try {
        glBegin(mode)
        body()
    } finally {
        glEnd()
    }
}
