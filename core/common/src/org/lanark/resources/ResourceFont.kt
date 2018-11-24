package org.lanark.resources

import kotlinx.io.core.*
import kotlinx.serialization.json.*
import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*

class ResourceFont(name: String, val location: FileLocation) :
    ResourceDescriptor<Image, Font>(name, resourceType) {

    lateinit var descriptor: FontDescriptor

    override fun bind(resource: Image, frame: Frame): Font {
        val texture: Texture = frame.bindTexture(resource)
        return Font(texture, descriptor)
    }

    override fun load(context: ResourceContext, progress: (Double) -> Unit): Image {
        val (path, fileSystem) = location
        val text = fileSystem.open(path, FileOpenMode.Read).use { it.input().readText() }
        descriptor = JSON.nonstrict.parse(FontDescriptor.serializer(), text)
        val texturePath = fileSystem.sibling(path, descriptor.imagePath)
        return context.loadImage(texturePath, fileSystem)
    }

    companion object {
        val resourceType = ResourceType("Font")
    }
}

fun ResourceContainer.font(name: String, file: String) =
    ResourceFont(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.font(path: String): Font = bindResource<Font, Image>(path, ResourceFont.resourceType)





