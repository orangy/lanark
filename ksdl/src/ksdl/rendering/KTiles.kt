package ksdl.rendering

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.geometry.*
import ksdl.system.*
import sdl2.*

class KTiles(val texture: KTexture, val tiles: Map<String, KTile>) : KManaged {
    override fun release() {
        texture.release()
    }

    override fun toString() = "Tiles $texture"
    operator fun get(name: String): KTile {
        return tiles[name] ?: throw Exception("Tile '$name' cannot be found")
    }
}

class KTile(val name: String, val texture: KTexture, val rectangle: KRect, val origin: KPoint) {
    val width get() = rectangle.width
    val height get() = rectangle.height

    override fun toString() = "Tile $name $rectangle"
}

fun KRenderer.draw(tile: KTile, position: KPoint) = memScoped {
    val srcRect = tile.rectangle
    val dstRect = SDL_Rect(position.x - tile.origin.x, position.y - tile.origin.y, tile.width, tile.height)
    SDL_RenderCopy(rendererPtr, tile.texture.texturePtr, SDL_Rect(srcRect), dstRect).checkSDLError("SDL_RenderCopy")
}

fun KRenderer.fill(tile: KTile, destinationRect: KRect) = memScoped {
    withClip(destinationRect) {
        val rect = alloc<SDL_Rect>().apply {
            w = tile.width
            h = tile.height
        }
        val srcRect = tile.rectangle

        for (x in destinationRect.left..destinationRect.right step tile.width) {
            for (y in destinationRect.top..destinationRect.bottom step tile.height) {
                rect.x = x
                rect.y = y
                SDL_RenderCopy(rendererPtr, tile.texture.texturePtr, SDL_Rect(srcRect), rect.ptr).checkSDLError("SDL_RenderCopy")
            }
        }
    }
}
