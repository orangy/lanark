package ksdl.ui

import ksdl.geometry.*
import ksdl.rendering.*
import ksdl.resources.*

class KDialog(private val resources: KResourceContainer) : UIScene() {
    private val background = resources.loadImage("background")

    override fun render(renderer: KRenderer, cache: KTextureCache) {
        renderer.renderBackground(cache)
    }

    private fun KRenderer.renderBackground(cache: KTextureCache) {
        val backgroundTx = background.toTexture(cache)
        val vscale = size.height.toDouble() / backgroundTx.size.height
        val hscale = size.width.toDouble() / backgroundTx.size.width
        val scale = maxOf(vscale, hscale)
        val destinationRect = KRect(0, 0, (backgroundTx.size.width * scale).toInt(), (backgroundTx.size.height * scale).toInt())
        draw(backgroundTx, destinationRect)
    }

}