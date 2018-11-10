package org.lanark.drawing

import org.khronos.webgl.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*
import org.w3c.dom.*
import kotlin.browser.*

typealias gl = WebGLRenderingContext

const val vsSource = """attribute vec4 aVertexPosition;
attribute vec2 aTextureCoord;
uniform mat4 uModelViewMatrix;
uniform mat4 uProjectionMatrix;
varying highp vec2 vTextureCoord;
void main(void) {
    gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;
    vTextureCoord = aTextureCoord;
}
"""

const val fsSource = """varying highp vec2 vTextureCoord;
    uniform sampler2D uSampler;
    void main(void) {
      gl_FragColor = texture2D(uSampler, vTextureCoord);
    }"""

actual class Texture(val image: HTMLImageElement) : Managed {
    override fun release() {
    }

    actual val size: Size
        get() = Size(image.width, image.height)
}

actual fun Frame.bindTexture(image: Image): Texture {
    return Texture(image.image)
}

fun isPowerOf2(value: Int) = (value and (value - 1)) == 0

actual fun Frame.draw(texture: Texture) {
    context.drawImage(texture.image, 0.0, 0.0)
}

actual fun Frame.draw(texture: Texture, sourceRect: Rect, destinationRect: Rect) {
    context.drawImage(
        texture.image,
        sourceRect.x.toDouble(),
        sourceRect.y.toDouble(),
        sourceRect.width.toDouble(),
        sourceRect.height.toDouble(),
        destinationRect.x.toDouble(),
        destinationRect.y.toDouble(),
        destinationRect.width.toDouble(),
        destinationRect.height.toDouble()
    )
}

actual fun Frame.draw(texture: Texture, destinationRect: Rect) {
    context.drawImage(
        texture.image,
        destinationRect.x.toDouble(),
        destinationRect.y.toDouble(),
        destinationRect.width.toDouble(),
        destinationRect.height.toDouble()
    )
}

actual fun Frame.draw(texture: Texture, position: Point) {
    draw(texture, Rect(position, texture.size))
}

actual fun Frame.draw(texture: Texture, position: Point, size: Size) {
    draw(texture, Rect(position, size))
}

