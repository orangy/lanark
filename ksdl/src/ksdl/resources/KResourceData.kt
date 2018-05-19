package ksdl.resources

import ksdl.data.*
import ksdl.io.*

class KResourceData(name: String, val location: KFileLocation) : KResource<KData>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KData {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            KData.load(file, fileSystem).also { progress(1.0) }
        }

    }

    companion object {
        val resourceType = KResourceType("Data")
    }
}

fun KResourceContainer.data(name: String, file: String) = KResourceData(name, KFileLocation(file, fileSystem)).also { register(it) }
fun KResourceContext.loadData(path: String) = loadResource<KData>(path, KResourceData.resourceType)

