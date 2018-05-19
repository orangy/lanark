package ksdl.system

class KVersion(val major: Int, val minor: Int, val patch: Int, val revision: String? = null) {
    override fun toString(): String = "$major.$minor.$patch${revision?.let { " [$it]" } ?: ""}"
}