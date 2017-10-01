import ksdl.resources.*
import sdl2.*
import ksdl.system.*

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

    val renderer = window.renderer()

    val resources = KResourceScope() {
        image("cursor", "cursor.png")
        scope("welcome") {
            image("background-image", "welcome-background.png")
            music("background-music", "welcome-music.ogg")
        }
        scope("ui") {
            image("background", "ui-background.png")
            tiles("elements", "ui-tileset") {
                tile("upper-left", 856, 189, 24, 24)
            }
        }
    }

    KPlatform.activeCursor = KPlatform.createCursor(resources.loadImage("cursor"), 0, 0)
    resources.release("cursor")
    val background = resources.loadImage("welcome/background-image").toTexture(renderer)
    val backgroundMusic = resources.loadMusic("welcome/background-music")

    val executor = KTaskExecutorIterative()
    val events = KEvents().apply {
        windowEvents.subscribe {
            if (it is KEventWindowResized) {
                renderer.size = KSize(it.width, it.height)
            }
        }
        appEvents.subscribe {
            if (it is KEventAppQuit)
                executor.stop()
        }
    }

    executor.beforeIteration.subscribe {
        events.pollEvents()
    }

    executor.submit {
        backgroundMusic.play()
    }

    var frames = 0
    val clock = KClock()
    executor.afterIteration.subscribe {
        frames++
        renderer.clear(Colors.BLACK)
        val vscale = window.size.height.toDouble() / background.size.height
        val hscale = window.size.width.toDouble() / background.size.width
        val scale = maxOf(vscale, hscale)
        val destinationRect = KRect(0, 0, (background.size.width * scale).toInt(), (background.size.height * scale).toInt())

        // logger.trace("BG: ${background.size}, WND: ${window.size} RND: ${renderer.size}: $scale -> $destinationRect")
        renderer.draw(background, destinationRect)
        renderer.present()

        val seconds = clock.elapsedMillis()
        if (seconds > 1000) {
            clock.reset()
            window.title = "$title / FPS: ${frames}"
            frames = 0
        }
    }

    executor.run()

    resources.release()
    renderer.release()
    window.release()
    KPlatform.quit()
}
