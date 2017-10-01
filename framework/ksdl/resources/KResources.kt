package ksdl.resources

abstract class KResource(val name: String, val resourceType: KResourceType) {
    override fun toString() = "$resourceType($name)"
    abstract fun release()
}

class KResourceType(val name: String) {
    override fun toString() = "Resource $name"
}

