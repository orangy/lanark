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

class ResourceFont(name: String, val location: FileLocation) :
    ResourceDescriptor<Image, Font>(name, resourceType) {

    lateinit var fontFile: FontFile

    override fun bind(resource: Image, frame: Frame): Font {
        val texture: Texture = frame.bindTexture(resource)
        val kernings = fontFile.kerning.groupBy { it.second }
        val characters = fontFile.symbols.map { 
            FontCharacter(it.id, Rect(it.x, it.y, it.width, it.height), it.xoffest, it.yoffset, it.xadvance,
                kernings[it.id]?.map { FontCharacterKerning(it.first, it.amount) } ?: emptyList())
        }
        return Font(texture, name, fontFile.config.base, fontFile.config.charHeight, characters)
    }

    override fun load(context: ResourceContext, progress: (Double) -> Unit): Image {
        val (path, fileSystem) = location
        val text = fileSystem.open(path, FileOpenMode.Read).use { it.input().readText() }
        fontFile = Json.nonstrict.parse(FontFile.serializer(), text)
        val texturePath = fileSystem.sibling(path, fontFile.config.textureFile)
        return context.loadImage(texturePath, fileSystem)
    }

    companion object {
        val resourceType = ResourceType("Font")
    }
}

fun ResourceContainer.font(name: String, file: String) =
    ResourceFont(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.font(path: String): Font = bindResource<Font, Image>(path, ResourceFont.resourceType)

// This is JSON format as exported by FontBuilder. Not entirely sure what format is it, but it works well.
// Later will need to support more formats, but that requires custom parsers

@Serializable
data class FontFile(val config: FontConfig, val symbols: List<FontChar>, val kerning: List<FontKerning>)

@Serializable
data class FontConfig(val textureFile: String, val charHeight: Int, val base: Int)

@Serializable
data class FontKerning(val first: Int, val second: Int, val amount: Int)

@Serializable
data class FontChar(
    val id: Int,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val xadvance: Int,
    @Optional val xoffest: Int = 0,
    @Optional val yoffset: Int = 0
)