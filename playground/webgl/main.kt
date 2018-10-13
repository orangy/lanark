import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.playground.*

fun main() {
    val engine = Engine {
        enableEverything()
    }
    val frame = engine.attachFrame("gl")
    game(frame)
}