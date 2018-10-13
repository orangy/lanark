package org.lanark.resources

import org.lanark.application.*

abstract class ResourceDescriptor<TResource, out TBound>(val name: String, val resourceType: ResourceType) {
    override fun toString() = "$resourceType($name)"

    abstract fun load(context: ResourceContext, progress: (Double) -> Unit): TResource
    abstract fun bind(resource: TResource, frame: Frame): TBound
}

