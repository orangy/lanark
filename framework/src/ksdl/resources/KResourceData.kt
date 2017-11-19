package ksdl.resources

import ksdl.io.*

class KResourceData(name: String, val location: KFileLocation) : KResource<KData>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KData = TODO("Load data")

    companion object {
        val resourceType = KResourceType("Data")
    }
}

fun KResourceContainer.data(name: String, file: String) = KResourceData(name, KFileLocation(file, fileSystem)).also { register(it) }
fun KResourceContext.loadData(path: String) = loadResource<KData>(path, KResourceData.resourceType)

