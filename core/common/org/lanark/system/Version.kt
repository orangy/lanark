package org.lanark.system

data class Version(val major: Int, val minor: Int, val patch: Int, val revision: String? = null) {
    override fun toString(): String = "$major.$minor.$patch${if (revision.isNullOrEmpty()) "" else " [$revision]"}"

    companion object {
        fun parse(text: String): Version {
            val parts = text.split(".")
            val major = if (parts.size > 0) parts[0].toIntOrNull() else null
            val minor = if (parts.size > 1) parts[1].toIntOrNull() else null
            val patch = if (parts.size > 2) parts[2].toIntOrNull() else null
            return Version(major ?: 0, minor ?: 0, patch ?: 0)
        }
    }
}