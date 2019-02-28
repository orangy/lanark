package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.media.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lwjgl.glfw.GLFW.*

actual class Cursor(val cursorHandle: Long) : Managed {
    override fun release() {
        glfwDestroyCursor(cursorHandle)
    }

    override fun toString() = "Cursor [$cursorHandle]"
}

actual fun ResourceContext.createCursor(image: Image, hotX: Int, hotY: Int): Cursor? {
    return Cursor(glfwCreateCursor(image.imageBuffer, hotX, hotY)).also {
        logger.system("Created $it from $image, Tip:[$hotX, $hotY]")
    }
}

actual fun ResourceContext.createCursor(cursor: SystemCursor): Cursor? {
    return Cursor(glfwCreateStandardCursor(cursor.cursorId))
}

actual enum class SystemCursor(val cursorId: kotlin.Int) {
    Arrow(GLFW_ARROW_CURSOR),
    IBeam(GLFW_IBEAM_CURSOR),
    CrossHair(GLFW_CROSSHAIR_CURSOR),
    SizeWE(GLFW_HRESIZE_CURSOR),
    SizeNS(GLFW_VRESIZE_CURSOR),
    SizeAll(GLFW_CENTER_CURSOR),
    Hand(GLFW_HAND_CURSOR),
}