package ksdl

import sdl2.SDL_Texture
import kotlinx.cinterop.*

class KTexture(val texturePtr: CValuesRef<SDL_Texture>) {

}