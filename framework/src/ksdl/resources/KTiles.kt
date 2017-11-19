package ksdl.resources

import ksdl.rendering.*
import ksdl.system.*

class KTiles(val surface: KSurface) : KManaged {
    override fun release() {
        surface.release()
    }

    override fun toString() = "Tiles $surface"
}