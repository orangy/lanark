import ksdl.composition.*
import ksdl.events.*
import ksdl.geometry.*
import ksdl.rendering.*
import ksdl.resources.*
import ksdl.system.*

class WelcomeScene(private val resources: KResourceScope) : KScene {
    private val background = resources.loadImage("welcome/background-image")
    private val backgroundMusic = resources.loadMusic("welcome/background-music")
    private val normalCursor = resources.loadCursor("cursors/normal")
    private val hotCursor = resources.loadCursor("cursors/hot")

    private val item = resources.loadImage("welcome/item")
    private val itemPosition = KPoint(100, 100)
    private val itemRect = KRect(itemPosition, item.size)

    override fun activate(executor: KTaskExecutor) {
        KPlatform.activeCursor = normalCursor
        backgroundMusic.play()
    }

    override fun deactivate(executor: KTaskExecutor) {
        backgroundMusic.stop()
    }

    private fun renderItem(renderer: KRenderer, cache: KTextureCache) {
        val itemTx = item.toTexture(cache)
        renderer.draw(itemTx, itemPosition)
    }

    private fun renderBackground(renderer: KRenderer, cache: KTextureCache) {
        val backgroundTx = background.toTexture(cache)
        val vscale = renderer.size.height.toDouble() / backgroundTx.size.height
        val hscale = renderer.size.width.toDouble() / backgroundTx.size.width
        val scale = maxOf(vscale, hscale)
        val destinationRect = KRect(0, 0, (backgroundTx.size.width * scale).toInt(), (backgroundTx.size.height * scale).toInt())
        renderer.draw(backgroundTx, destinationRect)
    }

    override fun render(renderer: KRenderer, cache: KTextureCache) {
        renderer.clear(KColor.BLACK)

        renderBackground(renderer, cache)
        renderItem(renderer, cache)

        renderer.present()
    }

    override fun event(event: KEvent, executor: KTaskExecutor) {
        when (event) {
            is KEventMouseMotion -> {
                if (event.position in itemRect) {
                    KPlatform.activeCursor = hotCursor
                } else {
                    KPlatform.activeCursor = normalCursor
                }
            }
        }
    }

    override fun toString() = "WelcomeScene"
}