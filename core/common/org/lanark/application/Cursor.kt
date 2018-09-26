package org.lanark.application

import org.lanark.system.*

expect class Cursor : Managed {
}

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