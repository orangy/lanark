package ksdl.ui

import ksdl.geometry.*
import ksdl.rendering.*
import ksdl.resources.*

class KDialog(private val resources: KResourceContext) : UIScene() {
    private val background = resources.loadTexture("background")

    override fun render(renderer: KRenderer) {
        renderer.renderBackground()
    }

    private fun KRenderer.renderBackground() {
        val vscale = size.height.toDouble() / background.size.height
        val hscale = size.width.toDouble() / background.size.width
        val scale = maxOf(vscale, hscale)
        val destinationRect = KRect(0, 0, (background.size.width * scale).toInt(), (background.size.height * scale).toInt())
        draw(background, destinationRect)
    }

}