package ksdl

import sdl2.SDL_Texture
import kotlinx.cinterop.*

class KTexture(val texturePtr: CPointer<SDL_Texture>) {

}