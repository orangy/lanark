package ksdl.resources

import ksdl.system.*

abstract class KResource<TResource>(val name: String, val resourceType: KResourceType) : KManaged {
    override fun toString() = "$resourceType($name)"

    abstract fun load(progress: (Double) -> Unit): TResource
}

class KResourceType(val name: String) {
    override fun toString() = "Resource $name"
}

