import ksdl.composition.*
import ksdl.resources.*
import ksdl.system.*
import sdl2.*

fun main(args: Array<String>) {
    KPlatform.init {
        logger = KLoggerConsole()
        enableEverything()
    }
    Mix_OpenAudio(MIX_DEFAULT_FREQUENCY, MIX_DEFAULT_FORMAT.toShort(), 1, 4096).checkSDLError("Mix_OpenAudio")

    val title = "Kotlin SDL2 Demo"
    val window = KPlatform.createWindow(title, 800, 600, windowFlags = SDL_WINDOW_ALLOW_HIGHDPI or SDL_WINDOW_SHOWN).apply {
        minimumSize = KSize(800, 600)
        setBordered(true)
        setResizable(true)
    }

    val renderer = window.createRenderer()
    val executor = KTaskExecutorIterative()

    val resources = KResourceScope() {
        scope("cursors") {
            cursor("normal", "cursor.png", 0, 0)
            cursor("hot", "cursor_outline_red.png", 0, 0)
        }
        scope("welcome") {
            image("background-image", "welcome-background.png")
            music("background-music", "welcome-music.ogg")

            image("item", "object.png")
        }
        scope("ui") {
            image("background", "ui-background.png")
            tiles("elements", "ui-tileset") {
                tile("upper-left", 856, 189, 24, 24)
            }
        }
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

