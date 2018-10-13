import org.lanark.application.*
import org.lanark.playground.*

fun main() {
    val engine = Engine {
        enableEverything()
    }

    @Suppress("NAMED_ARGUMENTS_NOT_ALLOWED")
    val frame = engine.createFrame(
        "Frame", engine.displayWidth, engine.displayHeight,
        flags = FrameFlag.CreateResizable + FrameFlag.CreateHiDPI + FrameFlag.CreateVisible + FrameFlag.CreateFullscreenDesktop
    )
    game(frame)
}