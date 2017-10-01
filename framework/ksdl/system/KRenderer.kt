package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

class KRenderer(val window: KWindow, private val rendererPtr: CPointer<SDL_Renderer>) {
    var size: KSize
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_RenderGetLogicalSize(rendererPtr, w.ptr, h.ptr)
            KSize(w.value, h.value)
        }
        set(value) {
            SDL_RenderSetLogicalSize(rendererPtr, value.width, value.height)
            logger.system("Resized $this for window #${window.id} to $size")
        }

    init {
        size = window.size
        logger.system("Created $this for window #${window.id}")
    }

    fun clear(color: KColor = Colors.BLACK) {
        SDL_SetRenderDrawColor(rendererPtr, color.red.toByte(), color.green.toByte(), color.blue.toByte(), color.alpha.toByte()).checkSDLError("SDL_SetRenderDrawColor")
        SDL_RenderClear(rendererPtr).checkSDLError("SDL_RenderClear")
    }

    fun present() {
        SDL_RenderPresent(rendererPtr)
    }

    fun release() {
        SDL_DestroyRenderer(rendererPtr)
        logger.system("Released $this")
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

    fun draw(texture: KTexture, position: KPoint) = memScoped {
        SDL_RenderCopy(rendererPtr, texture.texturePtr, null, SDL_Rect(KRect(position, texture.size)))
    }

    fun createTexture(surface: KSurface): KTexture {
        val texturePtr = SDL_CreateTextureFromSurface(rendererPtr, surface.surfacePtr).checkSDLError("SDL_CreateTextureFromSurface")
        return KTexture(texturePtr)
    }

    fun loadTexture(path: String): KTexture {
        val texture = IMG_LoadTexture(rendererPtr, path).checkSDLError("IMG_LoadTexture")
        logger.system("Loaded image into texture: $path")
        return KTexture(texture)
    }

    override fun toString() = "Renderer ${rendererPtr.rawValue}"

}

fun KSurface.toTexture(renderer: KRenderer) = renderer.createTexture(this)