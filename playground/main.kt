import ksdl.composition.*
import ksdl.diagnostics.*
import ksdl.events.*
import ksdl.geometry.*
import ksdl.resources.*
import ksdl.system.*
import ksdl.ui.*
import sdl2.*

fun main(args: Array<String>) {
    KPlatform.init {
        logger = consoleLogger {
            color(KLogCategory.System, "\u001B[0;37m")
            color(KLogCategory.Info, "\u001B[0;34m")
            color(KLogCategory.Warn, "\u001B[0;33m")
            color(KLogCategory.Error, "\u001B[0;31m")
            color(KSceneApplication.LogCategory, "\u001B[0;35m")
            color(KEvents.LogCategory, "\u001B[0;36m")
        }
        enableEverything()
    }

    val title = "Kotlin SDL2 Demo"
    val window = KWindow.create(title, 800, 600, windowFlags = SDL_WINDOW_ALLOW_HIGHDPI or SDL_WINDOW_SHOWN or SDL_WINDOW_OPENGL).apply {
        minimumSize = KSize(800, 600)
        setBordered(true)
        setResizable(true)
    }
    val renderer = window.createRenderer()

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
    }.bind(renderer)

    val uiResources = resources.loadScope("ui")

    val executor = KTaskExecutorIterative()
    var frames = 0
    val clock = KClock()
    executor.after.subscribe {
        frames++
        val seconds = clock.elapsedMillis()
        if (seconds > 1000) {
            clock.reset()
            window.title = "$title / FPS: $frames"
            frames = 0
        }
    }

    val application = KSceneApplication(executor, renderer)

    val dialog = KDialog(KRect(300, 130, 212, 234), uiResources)
    val welcome = WelcomeScene(resources)

    application.scene = KSceneLayered(listOf(welcome, dialog))

    application.run()

    uiResources.release()
    renderer.release()
    window.release()
    KPlatform.quit()
}

