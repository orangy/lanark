package ksdl

import sdl2.SDL_Cursor
import kotlinx.cinterop.*
import sdl2.SDL_SetCursor

class KCursor(val cursorPtr: CPointer<SDL_Cursor>)