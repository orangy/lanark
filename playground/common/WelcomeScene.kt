import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.ui.*

class WelcomeScene(private val application: SceneApplication,
                   private val nextScene: Scene,
                   resources: ResourceContext) : Scene {
    private val logo = resources.loadTexture("logo/logo")
    override fun activate(frame: Frame) {
    }

    override fun deactivate(frame: Frame) {
    }

    override fun render(frame: Frame) {
        frame.clear(Color.ALMOST_BLACK)
        val offset = frame.size / 2.0 - logo.size / 4.0
        frame.draw(logo, Point(offset.width, offset.height), logo.size / 2.0)
    }

    override fun event(frame: Frame, event: Event): Boolean {
        if (event is EventMouseButtonDown) {
            application.scene = nextScene
            return true
        }
        return false
    }
}