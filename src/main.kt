import ksdl.resources.*
import sdl2.*
import ksdl.system.*

/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

fun main(args: Array<String>) {
    KPlatform.init {
        logger = KLoggerConsole()
        enableEverything()
    }

    val cursor = KPlatform.loadSurface("cursor.png")
    KPlatform.activeCursor = KPlatform.createCursor(cursor, 0, 0)
    cursor.destroy()


    val title = "Kotlin SDL2 Demo"
    val window = KPlatform.createWindow(title, 800, 600, windowFlags = SDL_WINDOW_ALLOW_HIGHDPI or SDL_WINDOW_SHOWN).apply {
        minimumSize = KSize(800, 600)
        setBordered(true)
        setResizable(true)
    }

    val renderer = window.renderer()

    val resources = KResourceScope {
        scope("welcome") {
            image("background", "fortress.png")
        }
    }

    val background = resources.getImage("welcome/background").toTexture(renderer)

    val loop = KEventLoop()
    var frames = 0
    var start = KTime.now()
    val renderTask = {
        frames++
        renderer.clear(Colors.BLACK)
        val vscale = window.size.height.toDouble() / background.size.height
        val hscale = window.size.width.toDouble() / background.size.width
        val scale = maxOf(vscale, hscale)
        val destinationRect = KRect(0, 0, (background.size.width * scale).toInt(), (background.size.height * scale).toInt())

        // logger.trace("BG: ${background.size}, WND: ${window.size} RND: ${renderer.size}: $scale -> $destinationRect")
        renderer.draw(background, destinationRect)
        renderer.present()
        loop.submitSelf()
        val seconds = KTime.now().value - start.value
        if (seconds > 0) {
            start = KTime.now()
            window.title = "$title / FPS: ${frames}"
            frames = 0
        }
    }

    loop.submit(renderTask)
    loop.windowEvents.subscribe {
        if (it is KEventWindowResized) {
            renderer.size = KSize(it.width, it.height)
        }
    }
    loop.run()
    renderer.destroy()
    window.destroy()
    KPlatform.destroy()
}
