package org.lanark.drawing

import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import org.lanark.system.*
import sdl2.*

actual class Canvas(private val engine: Engine, internal val surfacePtr: CPointer<SDL_Surface>) : Managed {
    override fun release() {
        SDL_FreeSurface(surfacePtr)
        engine.logger.system("Released $this")
    }

    actual val size: Size get() = Size(surfacePtr.pointed.w, surfacePtr.pointed.h)

    actual var blendMode: BlendMode
        get() = memScoped {
            val mode = alloc<UIntVar>()
            SDL_GetSurfaceBlendMode(surfacePtr, mode.ptr).sdlError("SDL_GetSurfaceBlendMode")
            when (mode.value) {
                SDL_BLENDMODE_NONE -> BlendMode.None
                SDL_BLENDMODE_MOD -> BlendMode.Mod
                SDL_BLENDMODE_ADD -> BlendMode.Add
                SDL_BLENDMODE_BLEND -> BlendMode.Blend
                else -> throw EngineException("Unknown blend mode ${mode.value}")
            }
        }
        set(value) {
            val mode = when (value) {
                BlendMode.None -> SDL_BLENDMODE_NONE
                BlendMode.Blend -> SDL_BLENDMODE_BLEND
                BlendMode.Add -> SDL_BLENDMODE_ADD
                BlendMode.Mod -> SDL_BLENDMODE_MOD
            }
            SDL_SetSurfaceBlendMode(surfacePtr, mode).sdlError("SDL_SetSurfaceBlendMode")
        }

    actual fun blit(source: Canvas) {
        SDL_UpperBlit(source.surfacePtr, null, surfacePtr, null).sdlError("SDL_UpperBlit")
    }

    actual fun blit(source: Canvas, sourceRect: Rect, destination: Point) = memScoped {
        val destinationRect = Rect(destination, sourceRect.size)
        SDL_UpperBlit(
            source.surfacePtr,
            SDL_Rect(sourceRect),
            surfacePtr,
            SDL_Rect(destinationRect)
        ).sdlError("SDL_UpperBlit")
    }

    actual fun blitScaled(source: Canvas) {
        SDL_UpperBlitScaled(source.surfacePtr, null, surfacePtr, null).sdlError("SDL_UpperBlit")
    }

    actual fun blitScaled(source: Canvas, sourceRect: Rect, destinationRect: Rect) = memScoped {
        SDL_UpperBlitScaled(
            source.surfacePtr,
            SDL_Rect(sourceRect),
            surfacePtr,
            SDL_Rect(destinationRect)
        ).sdlError("SDL_UpperBlit")
    }

    actual fun fill(color: Color) {
        SDL_FillRect(
            surfacePtr,
            null,
            color.toRawColor(surfacePtr.pointed.format)
        ).sdlError("SDL_UpperBlit")
    }

    actual fun fill(color: Color, rectangle: Rect) = memScoped {
        SDL_FillRect(
            surfacePtr,
            SDL_Rect(rectangle),
            color.toRawColor(surfacePtr.pointed.format)
        ).sdlError("SDL_UpperBlit")
    }

    override fun toString() = "Surface ${surfacePtr.rawValue}"
}

