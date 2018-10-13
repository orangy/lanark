package org.lanark.media

import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*
import sdl2.*

actual class Image(private val logger: Logger, internal val surfacePtr: CPointer<SDL_Surface>) : Managed {
    override fun release() {
        SDL_FreeSurface(surfacePtr)
        logger.system("Released $this")
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

    actual fun blit(source: Image) {
        SDL_UpperBlit(source.surfacePtr, null, surfacePtr, null).sdlError("SDL_UpperBlit")
    }

    actual fun blit(source: Image, sourceRect: Rect, destination: Point) = memScoped {
        val destinationRect = Rect(destination, sourceRect.size)
        SDL_UpperBlit(
            source.surfacePtr,
            SDL_Rect(sourceRect),
            surfacePtr,
            SDL_Rect(destinationRect)
        ).sdlError("SDL_UpperBlit")
    }

    actual fun blitScaled(source: Image) {
        SDL_UpperBlitScaled(source.surfacePtr, null, surfacePtr, null).sdlError("SDL_UpperBlit")
    }

    actual fun blitScaled(source: Image, sourceRect: Rect, destinationRect: Rect) = memScoped {
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

    override fun toString() = "Canvas ${surfacePtr.rawValue}"
}

actual fun ResourceContext.loadImage(path: String, fileSystem: FileSystem): Image {
    return fileSystem.open(path, FileOpenMode.Read).use { file ->
        val surfacePtr = IMG_Load_RW(file.handle, 0).sdlError("IMG_Load_RW")
        Image(logger, surfacePtr).also {
            logger.system("Loaded $it from $path at $fileSystem")
        }
    }
}

actual fun ResourceContext.createImage(size: Size, bitsPerPixel: Int): Image {
    val surface =
        SDL_CreateRGBSurface(0, size.width, size.height, bitsPerPixel, 0, 0, 0, 0).sdlError("SDL_CreateRGBSurface")
    return Image(logger, surface).also {
        logger.system("Created $it")
    }
}