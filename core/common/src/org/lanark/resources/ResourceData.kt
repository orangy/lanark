package org.lanark.resources

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.io.*
import org.lanark.system.*

class ResourceData(name: String, val location: FileLocation) 
    : ResourceDescriptor<Data, Nothing?>(name, resourceType) {
    
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Data {
        val (file, fileSystem) = location
        return fileSystem.open(file, FileOpenMode.Read).use {
            Data().also {
                context.logger.system("Loaded $it from $file at $fileSystem")
                progress(1.0)
            }
        }
    }

    override fun bind(resource: Data, frame: Frame): Nothing? = null

    companion object {
        val resourceType = ResourceType("Data")
    }
}

fun ResourceContainer.data(name: String, file: String) =
    ResourceData(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.loadData(path: String) = loadResource<Data>(path, ResourceData.resourceType)

class Data : Managed {
    override fun release() {}

    override fun toString() = "Data"
}