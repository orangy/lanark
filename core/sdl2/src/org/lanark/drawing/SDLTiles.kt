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
    SDL_RenderCopy(rendererPtr, tile.texture.texturePtr, SDL_Rect(tile.rectangle), SDL_Rect(destinationRect)).sdlError("SDL_RenderCopy")
}