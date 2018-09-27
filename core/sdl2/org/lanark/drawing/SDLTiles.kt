package org.lanark.drawing

import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import sdl2.*

actual fun Frame.draw(tile: Tile, position: Point) = memScoped {
    val srcRect = tile.rectangle
    val dstRect = SDL_Rect(position.x - tile.origin.x, position.y - tile.origin.y, tile.width, tile.height)
    SDL_RenderCopy(rendererPtr, tile.texture.texturePtr, SDL_Rect(srcRect), dstRect).sdlError("SDL_RenderCopy")
}

actual fun Frame.fill(tile: Tile, destinationRect: Rect) = memScoped {
    clip(destinationRect) {
        val rect = alloc<SDL_Rect>().apply {
            w = tile.width
            h = tile.height
        }
        val srcRect = tile.rectangle

        for (x in destinationRect.left..destinationRect.right step tile.width) {
            for (y in destinationRect.top..destinationRect.bottom step tile.height) {
                rect.x = x
                rect.y = y
                SDL_RenderCopy(rendererPtr, tile.texture.texturePtr, SDL_Rect(srcRect), rect.ptr).sdlError("SDL_RenderCopy")
            }
        }
    }
}