package org.lanark.resources

abstract class Resource<out TResource>(val name: String, val resourceType: ResourceType) {
    override fun toString() = "$resourceType($name)"

    abstract fun load(
        context: ResourceContext,
        progress: (Double) -> Unit
    ): TResource
}

