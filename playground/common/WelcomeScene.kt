import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.ui.*

class WelcomeScene(val engine: Engine, private val resources: ResourceContext) : Scene {
    private val background = resources.loadTexture("welcome/background-image")
    //private val backgroundMusic = resources.loadMusic("welcome/background-music")
/*
    private val normalCursor = resources.loadCursor("cursors/normal")
    private val hotCursor = resources.loadCursor("cursors/hot")
*/

    private val item = resources.loadTexture("welcome/item")
    private val itemPosition = Point(10, 10)
    private val itemRect = Rect(itemPosition, item.size)

    override fun activate(frame: Frame) {
        //engine.activeCursor = normalCursor
        //backgroundMusic.play()
    }

    override fun deactivate(frame: Frame) {
        //backgroundMusic.stop()
    }

    private fun renderItem(frame: Frame) {
        frame.draw(item, itemPosition)
    }

    private fun renderBackground(frame: Frame) {
        val vscale = frame.canvasSize.height.toDouble() / background.size.height
        val hscale = frame.canvasSize.width.toDouble() / background.size.width
        val scale = maxOf(vscale, hscale)
        val destinationRect =
            Rect(0, 0, (background.size.width * scale).toInt(), (background.size.height * scale).toInt())
        frame.draw(background, destinationRect)
    }

    override fun render(frame: Frame) {
        renderBackground(frame)
        renderItem(frame)
    }

    override fun event(frame: Frame, event: Event): Boolean {
        when (event) {
            is EventMouseMotion -> {
/*
                if (event.position in itemRect) {
                    engine.activeCursor = hotCursor
                } else {
                    engine.activeCursor = normalCursor
                }
*/
            }
        }
        return true
    }

    override fun toString() = "WelcomeScene"
}