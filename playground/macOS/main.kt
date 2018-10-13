import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.playground.*

fun main() {
    val engine = Engine {
        enableEverything()
    }

    @Suppress("NAMED_ARGUMENTS_NOT_ALLOWED")
    val frame = engine.createFrame(
        "Frame", 800, 600,
        flags = FrameFlag.CreateResizable + FrameFlag.CreateHiDPI + FrameFlag.CreateVisible
    ).apply {
        minimumSize = Size(800, 600)
    }

    game(frame)
}