import ksdl.composition.*
import ksdl.events.*
import ksdl.resources.*
import ksdl.system.*

class WelcomeScene(private val resources: KResourceScope) : KScene {
    private val item = resources.loadImage("welcome/item")
    private val background = resources.loadImage("welcome/background-image")
    val itemPosition = KPoint(100, 100)

    override fun activate(executor: KTaskExecutor) {
        KPlatform.activeCursor = resources.loadCursor("cursors/normal")
        val backgroundMusic = resources.loadMusic("welcome/background-music")
        executor.submit {
            backgroundMusic.play()
        }
    }

    override fun deactivate(executor: KTaskExecutor) {

    }

    fun renderItem(renderer: KRenderer, cache: KTextureCache) {
        val itemTx = item.toTexture(cache)
        renderer.draw(itemTx, itemPosition)
    }

    fun renderBackground(renderer: KRenderer, cache: KTextureCache) {
        val backgroundTx = background.toTexture(cache)
        val vscale = renderer.size.height.toDouble() / backgroundTx.size.height
        val hscale = renderer.size.width.toDouble() / backgroundTx.size.width
        val scale = maxOf(vscale, hscale)
        val destinationRect = KRect(0, 0, (backgroundTx.size.width * scale).toInt(), (backgroundTx.size.height * scale).toInt())
        renderer.draw(backgroundTx, destinationRect)
    }

    override fun render(renderer: KRenderer, cache: KTextureCache) {
        renderer.clear(Colors.BLACK)

        renderBackground(renderer, cache)
        renderItem(renderer, cache)

        renderer.present()
    }

    override fun keyboard(event: KEventKey, executor: KTaskExecutor) {
    }

    override fun mouse(event: KEventMouse, executor: KTaskExecutor) {
    }

    override fun toString() = "WelcomeScene"
}