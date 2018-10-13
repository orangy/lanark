package org.lanark.application

import org.lanark.system.*
import org.lwjgl.glfw.GLFW.*

actual class Cursor(val cursorHandle: Long) : Managed {
    override fun release() {
        glfwDestroyCursor(cursorHandle)
    }

    override fun toString() = "Cursor ${cursorHandle}"
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