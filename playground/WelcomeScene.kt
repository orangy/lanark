import ksdl.composition.*
import ksdl.events.*
import ksdl.geometry.*
import ksdl.rendering.*
import ksdl.resources.*
import ksdl.system.*

class WelcomeScene(private val resources: KResourceContext) : KScene {
    private val background = resources.loadTexture("welcome/background-image")
    private val backgroundMusic = resources.loadMusic("welcome/background-music")
    private val normalCursor = resources.loadCursor("cursors/normal")
    private val hotCursor = resources.loadCursor("cursors/hot")

    private val item = resources.loadTexture("welcome/item")
    private val itemPosition = KPoint(100, 100)
    private val itemRect = KRect(itemPosition, item.size)

    override fun activate(executor: KTaskExecutor) {
        KPlatform.activeCursor = normalCursor
        //backgroundMusic.play()
    }

    override fun deactivate(executor: KTaskExecutor) {
        //backgroundMusic.stop()
    }

    private fun renderItem(renderer: KRenderer) {
        renderer.draw(item, itemPosition)
    }

    private fun renderBackground(renderer: KRenderer) {
        val vscale = renderer.size.height.toDouble() / background.size.height
        val hscale = renderer.size.width.toDouble() / background.size.width
        val scale = maxOf(vscale, hscale)
        val destinationRect = KRect(0, 0, (background.size.width * scale).toInt(), (background.size.height * scale).toInt())
        renderer.draw(background, destinationRect)
    }

    override fun render(renderer: KRenderer) {
        renderBackground(renderer)
        renderItem(renderer)
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