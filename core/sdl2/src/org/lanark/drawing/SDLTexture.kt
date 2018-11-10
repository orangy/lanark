package org.lanark.drawing

import cnames.structs.SDL_Texture
import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*
import sdl2.*

actual class Texture(val engine: Engine, val texturePtr: CPointer<SDL_Texture>) : Managed {
    actual val size: Size
        get() = memScoped {
            val width = alloc<IntVar>()
            val height = alloc<IntVar>()
            SDL_QueryTexture(texturePtr, null, null, width.ptr, height.ptr)
            Size(width.value, height.value)
        }

    override fun release() {
        SDL_DestroyTexture(texturePtr)
        engine.logger.system("Released $this")
    }

    override fun toString() = "Texture ${texturePtr.rawValue}"
}

actual fun Frame.draw(texture: Texture) {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, null, null).sdlError("SDL_RenderCopy")
}

actual fun Frame.draw(texture: Texture, sourceRect: Rect, destinationRect: Rect) = memScoped {
    SDL_RenderCopy(
        rendererPtr,
        texture.texturePtr,
        SDL_Rect(sourceRect),
        SDL_Rect(destinationRect)
    ).sdlError("SDL_RenderCopy")
}

actual fun Frame.draw(texture: Texture, destinationRect: Rect) = memScoped {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, null, SDL_Rect(destinationRect)).sdlError("SDL_RenderCopy")
}

actual fun Frame.draw(texture: Texture, position: Point) = memScoped {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, null, SDL_Rect(position, texture.size)).sdlError("SDL_RenderCopy")
}

actual fun Frame.draw(texture: Texture, position: Point, size: Size) = memScoped {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, null, SDL_Rect(position, size)).sdlError("SDL_RenderCopy")
}

actual fun Frame.bindTexture(image: Image): Texture {
    val texturePtr = SDL_CreateTextureFromSurface(rendererPtr, image.surfacePtr)
        .sdlError("SDL_CreateTextureFromSurface")
    return Texture(engine, texturePtr).also {
        engine.logger.system("Created $it from $image")
    }
}

