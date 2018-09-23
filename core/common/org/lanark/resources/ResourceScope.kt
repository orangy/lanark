package org.lanark.resources

interface ResourceScope {
    fun findResource(path: String): Resource<*>
}

fun <TResource> ResourceScope.findResource(path: String, resourceType: ResourceType): Resource<TResource> {
    val resource = findResource(path)
    if (resource.resourceType != resourceType)
        throw ResourcesException("Resource '$resource' is not a ${resourceType.name}")

    @Suppress("UNCHECKED_CAST")
    return resource as Resource<TResource>
}

class ResourcesException(message: String) : Exception(message)
