package ksdl

import kotlinx.cinterop.*
import sdl2.*

class KCursor(val cursorPtr: CPointer<SDL_Cursor>)