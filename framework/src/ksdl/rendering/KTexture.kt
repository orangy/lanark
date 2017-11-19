package ksdl.rendering

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.geometry.*
import ksdl.io.*
import ksdl.system.*
import sdl2.*

class KTexture(val texturePtr: CPointer<SDL_Texture>) : KManaged {
    val size: KSize = memScoped {
        val width = alloc<IntVar>()
        val height = alloc<IntVar>()
        SDL_QueryTexture(texturePtr, null, null, width.ptr, height.ptr)
        KSize(width.value, height.value)
    }

    val width: Int get() = size.width
    val height: Int get() = size.height

    override fun release() {
        SDL_DestroyTexture(texturePtr)
        logger.system("Released $this")
    }

    override fun toString() = "Texture ${texturePtr.rawValue}"

    companion object {
        fun load(path: String, fileSystem: KFileSystem, renderer: KRenderer): KTexture {
            return fileSystem.open(path).use { file ->
                val surfacePtr = IMG_Load_RW(file.handle, 0).checkSDLError("IMG_Load_RW")
                try {
                    val texturePtr = SDL_CreateTextureFromSurface(renderer.rendererPtr, surfacePtr).checkSDLError("SDL_CreateTextureFromSurface")
                    KTexture(texturePtr).also {
                        logger.system("Loaded $it from $path at $fileSystem")
                    }
                } finally {
                    SDL_FreeSurface(surfacePtr)
                }
            }
        }
    }
}

fun KRenderer.draw(texture: KTexture) {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, null, null).checkSDLError("SDL_RenderCopy")
}

fun KRenderer.draw(texture: KTexture, sourceRect: KRect, destinationRect: KRect) = memScoped {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, SDL_Rect(sourceRect), SDL_Rect(destinationRect)).checkSDLError("SDL_RenderCopy")
}

fun KRenderer.draw(texture: KTexture, destinationRect: KRect) = memScoped {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, null, SDL_Rect(destinationRect)).checkSDLError("SDL_RenderCopy")
}

fun KRenderer.fill(texture: KTexture, destinationRect: KRect) = memScoped {
    SDL_RenderSetClipRect(rendererPtr, SDL_Rect(destinationRect))
    val rect = alloc<SDL_Rect>().apply {
        w = texture.width
        h = texture.height
    }

    for (x in destinationRect.left..destinationRect.right step texture.width) {
        for (y in destinationRect.top..destinationRect.bottom step texture.height) {
            rect.x = x
            rect.y = y
            SDL_RenderCopy(rendererPtr, texture.texturePtr, null, rect.ptr).checkSDLError("SDL_RenderCopy")
        }
    }
    SDL_RenderSetClipRect(rendererPtr, null)
}

fun KRenderer.draw(texture: KTexture, position: KPoint) = memScoped {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, null, SDL_Rect(position, texture.size)).checkSDLError("SDL_RenderCopy")
}

fun KRenderer.draw(texture: KTexture, position: KPoint, size: KSize) = memScoped {
    SDL_RenderCopy(rendererPtr, texture.texturePtr, null, SDL_Rect(position, size)).checkSDLError("SDL_RenderCopy")
}