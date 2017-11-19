package ksdl.resources

import ksdl.rendering.*

class KTiles(val surface: KSurface) {
    fun release() {
        surface.release()
    }

    override fun toString() = "Tiles $surface"
}