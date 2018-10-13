package org.lanark.application

import org.lanark.media.*
import org.lanark.resources.*
import org.lanark.system.*

expect class Cursor : Managed {
}

expect fun ResourceContext.createCursor(image: Image, hotX: Int, hotY: Int): Cursor?
expect fun ResourceContext.createCursor(cursor: SystemCursor): Cursor?

expect enum class SystemCursor {
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