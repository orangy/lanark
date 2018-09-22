package ksdl.resources

abstract class KResource<out TResource>(val name: String, val resourceType: KResourceType) {
    override fun toString() = "$resourceType($name)"

    abstract fun load(context: KResourceContext, progress: (Double) -> Unit): TResource
}

class KResourceType(val name: String) {
    override fun toString() = "Resource $name"
}

