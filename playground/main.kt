import ksdl.composition.*
import ksdl.diagnostics.*
import ksdl.events.*
import ksdl.geometry.*
import ksdl.resources.*
import ksdl.system.*
import sdl2.*

fun main(args: Array<String>) {
    KPlatform.init {
        logger = consoleLogger {
            color(KLogCategory.System, "\u001B[0;37m")
            color(KLogCategory.Info, "\u001B[0;34m")
            color(KLogCategory.Warn, "\u001B[0;33m")
            color(KLogCategory.Error, "\u001B[0;31m")
            color(KSceneComposer.LogCategory, "\u001B[0;35m")
            color(KEvents.LogCategory, "\u001B[0;36m")
        }
        enableEverything()
    }

    val title = "Kotlin SDL2 Demo"
    val window = KPlatform.createWindow(title, 800, 600, windowFlags = SDL_WINDOW_ALLOW_HIGHDPI or SDL_WINDOW_SHOWN or SDL_WINDOW_OPENGL).apply {
        minimumSize = KSize(800, 600)
        setBordered(true)
        setResizable(true)
    }

    val renderer = window.createRenderer()
    val executor = KTaskExecutorIterative()

    val resources = resources {
        scope("cursors") {
            cursor("normal", "cursor.png", 0, 0)
            cursor("hot", "cursor-outline-red.png", 0, 0)
        }
        scope("terrain") {
            image("grass", "grass.png")
            image("tree", "grass-tree.png")
            image("water", "water.png")

            image("selected", "tile-select.png")
            image("hover", "tile-hover.png")
        }
        scope("welcome") {
            image("background-image", "welcome-background.png")
            music("background-music", "welcome-music.ogg")

            image("item", "object.png")
        }
        scope("ui") {
            image("background", "ui-background.png")
            tiles("elements", "ui-tileset.png") {
                tile("dialog-border-upper-left", 856, 189, 24, 24)
            }
        }
    }

    resources.load {
        logger.trace("Loading resources: ${KMath.round(it, 2)}")
    }

    var frames = 0
    val clock = KClock()
    executor.after.subscribe {
        frames++
        val seconds = clock.elapsedMillis()
        if (seconds > 1000) {
            clock.reset()
            window.title = "$title / FPS: ${frames}"
            frames = 0
        }
    }

    val composer = KSceneComposer(executor, renderer)
    composer.scene = HexScene(resources)
    composer.run()

    resources.release()
    renderer.release()
    window.release()
    KPlatform.quit()
}

