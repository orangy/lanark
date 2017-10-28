package ksdl.resources

import ksdl.system.*

class KTiles(val surface: KSurface) {
    fun release() {
        surface.release()
    }

    override fun toString() = "Tiles $surface"
}