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
/*
    val minU = sourceRect.left.toFloat() / texture.width
    val maxU = sourceRect.right.toFloat() / texture.width
    val minV = sourceRect.top.toFloat() / texture.height // 1.0f - is because texture is upside down
    val maxV = sourceRect.bottom.toFloat() / texture.height
    prepareContextFor2D(texture)
    drawStrip(minU, minV, maxU, maxV, destinationRect)
*/
}

actual fun Frame.draw(texture: Texture, destinationRect: Rect) {
    context.drawImage(
        texture.image,
        destinationRect.x.toDouble(),
        destinationRect.y.toDouble(),
        destinationRect.width.toDouble(),
        destinationRect.height.toDouble()
    )
/*
    prepareContextFor2D(texture)
    drawStrip(0f, 0f, 1f, 1f, destinationRect)
*/
}

actual fun Frame.fill(texture: Texture, destinationRect: Rect) {
    context.drawImage(
        texture.image,
        destinationRect.x.toDouble(),
        destinationRect.y.toDouble(),
        destinationRect.width.toDouble(),
        destinationRect.height.toDouble()
    )

/*
    prepareContextFor2D(texture)
    drawStrip(0f, 0f, 1f, 1f, destinationRect)
*/
}

actual fun Frame.draw(texture: Texture, position: Point) {
    draw(texture, Rect(position, texture.size))
}

actual fun Frame.draw(texture: Texture, position: Point, size: Size) {
    draw(texture, Rect(position, size))
}

/*
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
*/

/*
private inline fun Frame.prepareContextFor2D(texture: Texture) {
    val logicalSize = size
    context.matrixMode(GL_PROJECTION)
    context.loadIdentity()
    glOrtho(0.0, logicalSize.width.toDouble(), 0.0, logicalSize.height.toDouble(), 0.0, 1.0)
    glMatrixMode(GL_MODELVIEW)
    glColor4f(1f, 1f, 1f, 1f)
    context.enable(gl.GL_BLEND)
    glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
    glBlendEquation(GL_FUNC_ADD)
    context.bindTexture(GL_TEXTURE_2D, texture.textureId)
}

private inline fun Frame.draw(mode: Int, body: () -> Unit) {
    try {
        context.begin(mode)
        body()
    } finally {
        glEnd()
    }
}

fun createShaderProgram(gl: WebGLRenderingContext, vertexSource: String, fragmentSource: String): WebGLProgram {
    val vertexShader = getShader(gl, WebGLRenderingContext.VERTEX_SHADER, vertexSource)
    val fragmentShader = getShader(gl, WebGLRenderingContext.FRAGMENT_SHADER, fragmentSource)

    val shaderProgram = gl.createProgram()
    gl.attachShader(shaderProgram, fragmentShader)
    gl.attachShader(shaderProgram, vertexShader)
    gl.linkProgram(shaderProgram)

    if (!gl.getProgramParameter(shaderProgram, WebGLRenderingContext.LINK_STATUS)) {
        throw RuntimeException("Could not initialize shaders")
    }

    return shaderProgram
}

private fun getShader(gl: WebGLRenderingContext, shaderType: Int, source: String): WebGLShader {
    val shader = gl.createShader(shaderType)
    gl.shaderSource(shader, source)
    gl.compileShader(shader)
    if (!gl.getShaderParameter(shader, WebGLRenderingContext.COMPILE_STATUS)) {
        throw RuntimeException(gl.getShaderInfoLog(shader))
    }
    return shader
}*/
