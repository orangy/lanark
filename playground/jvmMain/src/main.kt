import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.playground.*
import org.lanark.system.*
import org.lanark.ui.*
import org.lanark.xml.*

fun main() {
    val engine = Engine {
        consoleLogger {
            color(LoggerCategory.System, "\u001B[0;37m")
            color(LoggerCategory.Info, "\u001B[0;34m")
            color(LoggerCategory.Warn, "\u001B[0;33m")
            color(LoggerCategory.Error, "\u001B[0;31m")
            color(SceneApplication.LogCategory, "\u001B[0;35m")
            color(Engine.LogCategory, "\u001B[0;36m")
        }
        enableEverything()
    }

    @Suppress("NAMED_ARGUMENTS_NOT_ALLOWED")
    val frame = engine.createFrame(
        "Frame", 800, 600,
        flags = FrameFlag.CreateResizable + FrameFlag.CreateHiDPI + FrameFlag.CreateVisible
    ).apply {
        minimumSize = Size(800, 600)
    }
    
    engine.logger.switch(Engine.LogCategory, false)

    engine.run {
        game(frame)
    }
    engine.destroy()
}