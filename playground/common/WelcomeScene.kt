import kotlinx.coroutines.*
import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.ui.*
import kotlin.random.*

class WelcomeScene(val engine: Engine, private val resources: ResourceContext) : Scene {
    private val background = resources.loadTexture("welcome/background-image")
    //private val backgroundMusic = resources.loadMusic("welcome/background-music")
    private val normalCursor = resources.loadCursor("cursors/normal")
    private val hotCursor = resources.loadCursor("cursors/hot")
    private val tiles = resources.loadTiles("ui/elements")
    private val item = tiles["button"]

    class Shield(
        private val texture: Tile,
        position: Point,
        val minPosition: Int,
        val maxPosition: Int,
        val speed: Int
    ) {
        private var itemPosition = position

        suspend fun run() {
            while (true) {
                while(itemPosition.x < maxPosition) {
                    itemPosition += Vector(speed, 0)
                    yield()
                }
                while(itemPosition.x > minPosition) {
                    itemPosition -= Vector(speed, 0)
                    yield()
                }
            }
        }

        fun render(frame: Frame) {
            frame.draw(texture, itemPosition)
        }
    }
    
    private val minPosition = 10
    private val maxPosition = 600 - item.width

    private val items = List(5) {
        Shield(
            item,
            Point(Random.nextInt(minPosition, maxPosition), 5 + item.height * 2 * it),
            minPosition,
            maxPosition, 
            Random.nextInt(4)
        )
    }

    override fun activate(frame: Frame) {
        frame.cursor = normalCursor

        engine.executor.submit {
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
        return true
    }

    override fun toString() = "WelcomeScene"
}