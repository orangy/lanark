package org.lanark.application

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