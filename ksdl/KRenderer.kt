package ksdl

import kotlinx.cinterop.*
import sdl2.*

class KRenderer(val window: KWindow, val rendererPtr: CPointer<SDL_Renderer>) {
    init {
        logger.trace("Created renderer ${rendererPtr.rawValue} for window #${window.id}")
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
        logger.trace("Destroyed renderer ${rendererPtr.rawValue}")
    }

    fun draw(texture: KTexture) {
        SDL_RenderCopy(rendererPtr, texture.texturePtr, null, null)
    }

    fun draw(texture: KTexture, sourceRect: KRect, destinationRect: KRect) = memScoped {
        SDL_RenderCopy(rendererPtr, texture.texturePtr, SDL_Rect(sourceRect), SDL_Rect(destinationRect))
    }

    fun draw(texture: KTexture, destinationRect: KRect) = memScoped {
        SDL_RenderCopy(rendererPtr, texture.texturePtr, null, SDL_Rect(destinationRect))
    }

    fun createTexture(surface: KSurface): KTexture {
        val texturePtr = SDL_CreateTextureFromSurface(rendererPtr, surface.surfacePtr).checkSDLError("SDL_CreateTextureFromSurface")
        return KTexture(texturePtr)
    }

    fun loadTexture(path: String): KTexture {
        val texture = IMG_LoadTexture(rendererPtr, path).checkSDLError("IMG_LoadTexture")
        logger.trace("Loaded image into texture: $path")
        return KTexture(texture)
    }
}

fun KSurface.toTexture(renderer: KRenderer) = renderer.createTexture(this)
