package ksdl.rendering

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.geometry.*
import ksdl.io.*
import ksdl.system.*
import sdl2.*

class KSurface(internal val surfacePtr: CPointer<SDL_Surface>) : KManaged {
    override fun release() {
        SDL_FreeSurface(surfacePtr)
        logger.system("Released $this")
    }

    val size: KSize get() = KSize(surfacePtr.pointed.w, surfacePtr.pointed.h)

    var blendMode: BlendMode
        get() = memScoped {
            val mode = alloc<UIntVar>()
            SDL_GetSurfaceBlendMode(surfacePtr, mode.ptr).checkSDLError("SDL_GetSurfaceBlendMode")
            when (mode.value) {
                SDL_BLENDMODE_NONE -> BlendMode.None
                SDL_BLENDMODE_MOD -> BlendMode.Mod
                SDL_BLENDMODE_ADD -> BlendMode.Add
                SDL_BLENDMODE_BLEND -> BlendMode.Blend
                else -> throw KPlatformException("Unknown blend mode ${mode.value}")
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
        return SDL_MapRGBA(surfacePtr.pointed.format, red, green, blue, alpha)
    }

    override fun toString() = "Surface ${surfacePtr.rawValue}"

    companion object {
        fun load(path: String, fileSystem: KFileSystem): KSurface {
            return fileSystem.open(path).use { file ->
                val surfacePtr = IMG_Load_RW(file.handle, 0).checkSDLError("IMG_Load_RW")
                KSurface(surfacePtr).also {
                    logger.system("Loaded $it from $path at $fileSystem")
                }
            }
        }

        fun create(size: KSize, bitsPerPixel: Int): KSurface {
            val surface = SDL_CreateRGBSurface(0, size.width, size.height, bitsPerPixel, 0, 0, 0, 0).checkSDLError("SDL_CreateRGBSurface")
            return KSurface(surface).also {
                logger.system("Created $it")
            }
        }
    }
}

