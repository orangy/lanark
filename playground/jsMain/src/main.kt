import org.lanark.application.*
import org.lanark.playground.*

fun main() {
    val engine = Engine {
        enableEverything()
    }
    val frame = engine.attachFrame("gl")

    engine.run {
        game(frame)
    }
    engine.destroy()
}