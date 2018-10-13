package org.lanark.application

import org.lanark.media.*
import org.lanark.resources.*
import org.lanark.system.*

actual class Cursor : Managed {
    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual enum class SystemCursor {
    Arrow,
    IBeam,
    CrossHair,
    SizeWE,
    SizeNS,
    SizeAll,
    Hand,

/*
    Wait,
    WaitArrow,
    SizeNWSE,
    SizeNESW,
    No,
*/
}

actual fun ResourceContext.createCursor(image: Image, hotX: Int, hotY: Int): Cursor? {
    return null
}

actual fun ResourceContext.createCursor(cursor: SystemCursor): Cursor? {
    return null
}

