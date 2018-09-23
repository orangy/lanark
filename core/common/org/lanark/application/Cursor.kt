package org.lanark.application

import org.lanark.system.*

expect class Cursor : Managed {
}

expect enum class SystemCursor {
    Arrow,
    IBeam,
    Wait,
    CrossHair,
    WaitArrow,
    SizeNWSE,
    SizeNESW,
    SizeWE,
    SizeNS,
    SizeAll,
    No,
    Hand
}