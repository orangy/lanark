package ksdl.resources

import ksdl.diagnostics.*
import ksdl.io.*

class KResourceData(name: String, val file: String) : KResource(name, resourceType) {
    fun load(fileSystem: KFileSystem): KData = TODO("Load data")
    override fun release() {}

    companion object {
        val resourceType = KResourceType("Data")
    }
}

fun KResourceScope.data(name: String, file: String) = KResourceData(name, file).also { register(it) }

fun KResourceScope.loadData(path: String): KData {
    val resource = findResource(path)
    if (resource.resourceType != KResourceData.resourceType)
        throw KPlatformException("Resource '$resource' is not a Data")
    return (resource as KResourceData).load(fileSystem)
}

