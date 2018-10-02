package org.lanark.events

import kotlinx.cinterop.*
import sdl2.*

actual fun getScanCodeName(scanCode: UInt): String? {
    return SDL_GetScancodeName(scanCode)?.toKString()
}