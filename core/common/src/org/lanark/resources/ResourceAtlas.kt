package org.lanark.resources

import kotlinx.io.core.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*

class ResourceAtlas(name: String, val location: FileLocation) :
    ResourceDescriptor<Image, Tiles>(name, ResourceTiles.resourceType) {

    lateinit var atlas: Atlas

    override fun load(context: ResourceContext, progress: (Double) -> Unit): Image {
        val (path, fileSystem) = location
        val text = fileSystem.open(path, FileOpenMode.Read).use { it.input().readText() }
        atlas = Json.nonstrict.parse(Atlas.serializer(), text)
        val texturePath = fileSystem.sibling(path, atlas.imagePath)
        return context.loadImage(texturePath, fileSystem)
    }

    override fun bind(resource: Image, frame: Frame): Tiles {
        val texture: Texture = frame.bindTexture(resource)
        val tiles = atlas.textures.map { item ->
            Tile(item.name, texture, Rect(item.x, item.y, item.width, item.height))
        }.associateBy { it.name }
        return Tiles(texture, tiles)//.also { progress(1.0) }
    }

    companion object {
        val resourceType = ResourceType("Atlas")
    }

}

fun ResourceContainer.atlas(name: String, file: String) =
    ResourceAtlas(name, FileLocation(file, fileSystem)).also { register(it) }

@Serializable
data class Atlas(val imagePath: String, val textures: List<AtlasTexture>)

@Serializable
data class AtlasTexture(val name: String, val x: Int, val y: Int, val width: Int, val height: Int)

