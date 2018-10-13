package org.lanark.media

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lwjgl.glfw.*
import org.lwjgl.stb.*
import org.lwjgl.system.*

actual class Image(val imageBuffer: GLFWImage) : Managed {
    override fun release() {
        imageBuffer.close()
    }

    actual val size: Size
        get() = Size(imageBuffer.width(), imageBuffer.height())

    actual var blendMode: BlendMode = BlendMode.None

    actual fun blit(source: Image) {

    }

    actual fun blit(source: Image, sourceRect: Rect, destination: Point) {}
    actual fun blitScaled(source: Image) {}
    actual fun blitScaled(source: Image, sourceRect: Rect, destinationRect: Rect) {}
    actual fun fill(color: Color) {}
    actual fun fill(color: Color, rectangle: Rect) {}

    override fun toString() = "Image [${imageBuffer.address()}]"
}

actual fun ResourceContext.createImage(size: Size, bitsPerPixel: Int): Image {
    val image = GLFWImage.malloc()
    return Image(image).also {
        logger.system("Created $it ")
    }
}

actual fun ResourceContext.loadImage(path: String, fileSystem: FileSystem): Image {
    val image = GLFWImage.malloc()
    MemoryStack.stackPush().use { stack ->
        val w = stack.mallocInt(1)
        val h = stack.mallocInt(1)
        val comp = stack.mallocInt(1)
        STBImage.stbi_set_flip_vertically_on_load(false)
        val pixels = STBImage.stbi_load(path, w, h, comp, 4)
            ?: throw EngineException("Failed to load a texture file: ${STBImage.stbi_failure_reason()}")

        image.set(w.get(), h.get(), pixels)
        STBImage.stbi_image_free(pixels)

        return Image(image).also {
            logger.system("Loaded $it from $path at $fileSystem")
        }
    }
}