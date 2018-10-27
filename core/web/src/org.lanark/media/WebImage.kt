package org.lanark.media

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*
import org.w3c.dom.*
import kotlin.browser.*
import kotlin.coroutines.*

actual class Image(val image: HTMLImageElement) : Managed {
    override fun release() {}

    actual val size: Size
        get() = Size(image.width, image.height)

    actual var blendMode: BlendMode = BlendMode.Blend

    actual fun blit(source: Image) {}
    actual fun blit(source: Image, sourceRect: Rect, destination: Point) {}
    actual fun blitScaled(source: Image) {}
    actual fun blitScaled(source: Image, sourceRect: Rect, destinationRect: Rect) {}
    actual fun fill(color: Color) {}
    actual fun fill(color: Color, rectangle: Rect) {}
}

actual fun ResourceContext.createImage(size: Size, bitsPerPixel: Int): Image {
    val image = window.document.createElement("img") as HTMLImageElement
    image.width = size.width
    image.height = size.height
    return Image(image)
}

actual fun ResourceContext.loadImage(path: String, fileSystem: FileSystem): Image {
    val image = window.document.createElement("img") as HTMLImageElement
    logger.system("Requested image from $path")
    image.onload = {
        logger.system("Loaded image from $path")
    }
    image.src = path
    return Image(image)
}

// TODO: make resource loading suspendable activity with multiple things loading simultaneously
suspend fun HTMLImageElement.loadImage(src: String) : Unit = suspendCancellableCoroutine { coroutine ->
    onload = {
        onload = null
        coroutine.resume(Unit)
    }
    this.src = src
}