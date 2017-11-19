package ksdl.resources

import ksdl.system.*

abstract class KResource(val name: String, val resourceType: KResourceType) : KManaged {
    override fun toString() = "$resourceType($name)"
}

class KResourceType(val name: String) {
    override fun toString() = "Resource $name"
}

