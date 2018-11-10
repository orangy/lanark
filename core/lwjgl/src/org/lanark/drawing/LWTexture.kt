package org.lanark.drawing

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*
import org.lwjgl.glfw.*
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

actual fun Frame.bindTexture(image: Image): Texture {
    val texture = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, texture)
    glEnable(GL_TEXTURE_2D)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    MemoryStack.stackPush().use { stack ->
        val (width, height) = image.size
        
        /*
            glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
            glTexSubImage2D(GL_TEXTURE_2D, 0​, 0, 0, width​, height​, GL_BGRA, GL_UNSIGNED_BYTE, pixels);
         */
        val pixels = MemoryUtil.memGetAddress(image.imageBuffer.address() + GLFWImage.PIXELS)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels)
        return Texture(engine, texture, Size(width, height)).also {
            engine.logger.system("Created $it from $image")
        } 
        
    }
}

actual fun Frame.draw(texture: Texture) {
    prepareContextFor2D(texture)
    drawStrip(0f, 0f, 1f, 1f, Rect(0, 0, texture.width, texture.height))
}

actual fun Frame.draw(texture: Texture, sourceRect: Rect, destinationRect: Rect) {
    val minU = sourceRect.left.toFloat() / texture.width
    val maxU = sourceRect.right.toFloat() / texture.width
    val minV = sourceRect.top.toFloat() / texture.height 
    val maxV = sourceRect.bottom.toFloat() / texture.height
    prepareContextFor2D(texture)
    drawStrip(minU, minV, maxU, maxV, destinationRect)
}

actual fun Frame.draw(texture: Texture, destinationRect: Rect) {
    prepareContextFor2D(texture)
    drawStrip(0f, 0f, 1f, 1f, destinationRect)
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
    glEnable(GL_TEXTURE_2D)
}

private inline fun Frame.draw(mode: Int, body: () -> Unit) {
    try {
        glBegin(mode)
        body()
    } finally {
        glEnd()
    }
}
