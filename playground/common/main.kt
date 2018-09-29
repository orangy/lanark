import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lanark.ui.*

fun main() {
    println("Starting Lanark Demo…")
    val engine = Engine {
        consoleLogger {
            color(LoggerCategory.System, "\u001B[0;37m")
            color(LoggerCategory.Info, "\u001B[0;34m")
            color(LoggerCategory.Warn, "\u001B[0;33m")
            color(LoggerCategory.Error, "\u001B[0;31m")
            color(SceneApplication.LogCategory, "\u001B[0;35m")
            color(Events.LogCategory, "\u001B[0;36m")
        }
        enableEverything()
    }

    val title = "Lanark Demo"
    @Suppress("NAMED_ARGUMENTS_NOT_ALLOWED")
    val frame = engine.createFrame(title, 800, 600, flags = FrameFlag.CreateHiDPI + FrameFlag.CreateVisible).apply {
        minimumSize = Size(800, 600)
        setBordered(true)
        setResizable(true)
    }

    val ui = resources("ui") {
        texture("background", "ui-background.png")
        tiles("elements", "ui-tileset.png") {
            tile("border-top-left", 855, 188, 24, 24, 14, 14)
            tile("border-top", 893, 188, 72, 24, 0, 14)
            tile("border-top-right", 978, 188, 24, 24, 11, 14)
            tile("border-right", 978, 228, 24, 54, 11, 0)
            tile("border-bottom-right", 978, 294, 24, 24, 11, 11)
            tile("border-bottom", 893, 294, 72, 24, 0, 11)
            tile("border-bottom-left", 855, 294, 24, 24, 14, 11)
            tile("border-left", 855, 228, 24, 54, 14, 0)

            tile("button", 12, 126, 285, 54)
            tile("button-pressed", 12, 126 + 78, 285, 54)
            tile("button-hover", 12, 126 + 78 * 2, 285, 54)
            tile("button-disabled", 12, 126 + 78 * 3, 285, 54)
        }
    }

    val resources = resources("main") {
        scope("cursors") {
            cursor("normal", "cursor.png", 0, 0)
            cursor("hot", "cursor-outline-red.png", 0, 0)
        }

        scope("terrain") {
            texture("grass", "grass.png")
            texture("tree", "grass-tree.png")
            texture("water", "water.png")

            texture("selected", "tile-select.png")
            texture("hover", "tile-hover.png")
        }

        scope("welcome") {
            texture("background-image", "welcome-background.png")
            music("background-music", "welcome-music.ogg")

            texture("item", "object.png")
        }

        register(ui)
    }.bind(frame)

    val uiResources = resources.loadScope("ui")

    var frames = 0
    val clock = Clock()
    engine.executor.after.subscribe {
        frames++
        val seconds = clock.elapsedMillis()
        if (seconds > 1000u) {
            clock.reset()
            frame.title = "$title / FPS: $frames"
            frames = 0
        }

    }
    engine.logger.switch(Events.LogCategory, false)

    engine.events.window.filter<EventWindowClose>().subscribe {
        engine.postQuitEvent()
    }
    
    val application = SceneApplication(frame)
    val dialog = Dialog(
        Rect(140, 140, 412, 234), uiResources, listOf(
            Button(Point(20, 20), uiResources),
            Button(Point(20, 80), uiResources)
        )
    )
    val welcome = WelcomeScene(engine, resources)

    application.scene = SceneLayered("main", listOf(welcome, dialog))

    application.run()

    uiResources.release()
    frame.release()
    engine.quit()
}

