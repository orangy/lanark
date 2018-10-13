package org.lanark.resources

import org.lanark.diagnostics.*
import org.lanark.io.*
import org.lanark.system.*

class ResourceData(name: String, val location: FileLocation) : Resource<Data>(name, resourceType) {
    override fun load(
        context: ResourceContext,
        progress: (Double) -> Unit
    ): Data {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            fileSystem.open(file, FileOpenMode.Read).use {
                Data().also {
                    context.engine.logger.system("Loaded $it from $file at $fileSystem")
                    progress(1.0)
                }
            }
        }

    }

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