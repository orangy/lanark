package ksdl

import kotlinx.cinterop.*
import sdl2.*

class KSurface(val surfacePtr: CPointer<SDL_Surface>) {
    var blendMode: BlendMode
        get() = memScoped {
            val mode = alloc<IntVar>()
            SDL_GetSurfaceBlendMode(surfacePtr, mode.ptr).checkSDLError("SDL_GetSurfaceBlendMode")
            when (mode.value) {
                SDL_BLENDMODE_NONE -> BlendMode.None
                SDL_BLENDMODE_MOD -> BlendMode.Mod
                SDL_BLENDMODE_ADD -> BlendMode.Add
                SDL_BLENDMODE_BLEND -> BlendMode.Blend
                else -> throw KGraphicsException("Unknown blend mode ${mode.value}")
            }
        }
        set(value) {
            val mode = when (value) {
                BlendMode.None -> SDL_BLENDMODE_NONE
                BlendMode.Blend -> SDL_BLENDMODE_BLEND
                BlendMode.Add -> SDL_BLENDMODE_ADD
                BlendMode.Mod -> SDL_BLENDMODE_MOD
            }
            SDL_SetSurfaceBlendMode(surfacePtr, mode).checkSDLError("SDL_SetSurfaceBlendMode")
        }

    enum class BlendMode {
        None, Blend, Add, Mod
    }

    fun blit(source: KSurface) {
        SDL_UpperBlit(source.surfacePtr, null, surfacePtr, null).checkSDLError("SDL_UpperBlit")
    }

    fun blit(source: KSurface, sourceRect: KRect, destination: KPoint) = memScoped {
        val destinationRect = KRect(destination, sourceRect.size)
        SDL_UpperBlit(source.surfacePtr, SDL_Rect(sourceRect), surfacePtr, SDL_Rect(destinationRect)).checkSDLError("SDL_UpperBlit")
    }

    fun blitScaled(source: KSurface) {
        SDL_UpperBlitScaled(source.surfacePtr, null, surfacePtr, null).checkSDLError("SDL_UpperBlit")
    }

    fun blitScaled(source: KSurface, sourceRect: KRect, destinationRect: KRect) = memScoped {
        SDL_UpperBlitScaled(source.surfacePtr, SDL_Rect(sourceRect), surfacePtr, SDL_Rect(destinationRect)).checkSDLError("SDL_UpperBlit")
    }

    fun fill(color: KColor) {
        SDL_FillRect(surfacePtr, null, color.toRawColor()).checkSDLError("SDL_UpperBlit")
    }

    fun fill(color: KColor, rectangle: KRect) = memScoped {
        SDL_FillRect(surfacePtr, SDL_Rect(rectangle), color.toRawColor()).checkSDLError("SDL_UpperBlit")
    }

    private fun KColor.toRawColor(): Uint32 {
        return SDL_MapRGBA(surfacePtr.pointed.format, red.toByte(), green.toByte(), blue.toByte(), alpha.toByte())
    }

    fun destroy() {
        SDL_FreeSurface(surfacePtr)
    }
}

