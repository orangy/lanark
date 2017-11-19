package ksdl.resources

import ksdl.io.*

class KResourceData(name: String, val location: KFileLocation) : KResource<KData>(name, resourceType) {
    override fun load(progress: (Double) -> Unit): KData = TODO("Load data")
    override fun release() {}

    companion object {
        val resourceType = KResourceType("Data")
    }
}

fun KResourceContainer.data(name: String, file: String) = KResourceData(name, KFileLocation(file, fileSystem)).also { register(it) }

fun KResourceSource.loadData(path: String) = loadResource<KData>(path, KResourceData.resourceType)

