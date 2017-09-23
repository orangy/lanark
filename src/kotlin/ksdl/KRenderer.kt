package ksdl

import kotlinx.cinterop.CPointer
import sdl2.*

class KRenderer(val window: KWindow, val rendererPtr: CPointer<SDL_Renderer>) {
    init {
        val size = window.size
        SDL_RenderSetLogicalSize(rendererPtr, size.width, size.height)
    }

    fun clear(color: KColor = Colors.BLACK) {
        SDL_SetRenderDrawColor(rendererPtr, color.red.toByte(), color.green.toByte(), color.blue.toByte(), color.alpha.toByte()).checkSDLError("SDL_SetRenderDrawColor")
        SDL_RenderClear(rendererPtr).checkSDLError("SDL_RenderClear")
    }

    fun present() {
        SDL_RenderPresent(rendererPtr)
    }

    fun destroy() {
        SDL_DestroyRenderer(rendererPtr)
    }

    fun draw(texture: KTexture) {
        SDL_RenderCopy(rendererPtr, texture.texturePtr, null, null)
    }

    fun createTexture(surface: KSurface): KTexture {
        val texturePtr = SDL_CreateTextureFromSurface(rendererPtr, surface.surfacePtr).checkSDLError("SDL_CreateTextureFromSurface")
        return KTexture(texturePtr)
    }
}

