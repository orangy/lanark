import kotlinx.coroutines.*
import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.ui.*
import kotlin.random.*

class WelcomeScene(private val application: SceneApplication, resources: ResourceContext) : Scene {
    private val background = resources.loadTexture("welcome/background-image")
    //private val backgroundMusic = resources.loadMusic("welcome/background-music")
    private val normalCursor = resources.loadCursor("cursors/normal")
    private val hotCursor = resources.loadCursor("cursors/hot")
    private val tiles = resources.loadTiles("ui/elements")
    private val item = tiles["button"]

    private val minPosition = 10
    private val maxPosition = 600 - item.width - 10

    private val items = List(5) {
        Shield(
            item,
            Point(Random.nextInt(minPosition, maxPosition), 5 + item.height * 2 * it),
            minPosition,
            maxPosition,
            Random.nextInt(1, 5),
            normalCursor,
            hotCursor
        )
    }

    override fun activate(frame: Frame) {
        frame.cursor = normalCursor

        application.frame.engine.executor.submit {
            items.forEach {
                launch { it.run() }
            }
        }
        //backgroundMusic.play()
    }

    override fun deactivate(frame: Frame) {
        //backgroundMusic.stop()
    }

    private fun renderBackground(frame: Frame) {
        val scale = (frame.size / background.size).max()
        val destinationRect = Rect(Point(0, 0), background.size * scale)
        frame.draw(background, destinationRect)
    }

    override fun render(frame: Frame) {
        renderBackground(frame)
        items.forEach { it.render(frame) }
    }

    override fun event(frame: Frame, event: Event): Boolean {
        val handled = items.any { it.handle(frame, event) }
        if (!handled)
            frame.cursor = normalCursor
        return handled
    }

    override fun toString() = "WelcomeScene"
}