import kdsl.KEventLoop
import kotlinx.cinterop.*
import ksdl.*
import sdl2.*

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

    val sword = KPlatform.loadSurface("sword.png")
    KPlatform.activeCursor = KPlatform.createCursor(sword, 0, 0)
    sword.destroy()

    val title = "Kotlin SDL2 Demo"
    val window = KPlatform.createWindow(title, 100, 100, 800, 600)
    window.minimumSize = KSize(200, 200)
    window.maximumSize = KSize(1200, 800)
    window.setBordered(true)
    window.setResizable(true)

    val renderer = window.renderer()

    val sfc = KPlatform.createSurface(KSize(200, 200), 32)
    sfc.fill(Colors.BLUE)
    val tx2 = sfc.toTexture(renderer)
    sfc.destroy()

    val texture = renderer.loadTexture("tetris_all.bmp")
    val loop = KEventLoop()
    var x = 20
    var y = 20

    var frames = 0
    var start = KTime.now()
    val renderTask = {
        frames++
        renderer.clear(Colors.BLACK)
        renderer.draw(texture)
        renderer.draw(tx2, KRect(x, y, 50, 50))
        renderer.present()
        loop.submitSelf()
        val seconds = KTime.now().value - start.value
        if (seconds > 0) {
            start = KTime.now()
            window.title = "$title / FPS: ${frames}"
            frames = 0
        }
    }

    loop.keyEvents.subscribe { event ->
        if (event is KEventKeyDown) {
            when (event.scanCode) {
                SDL_SCANCODE_LEFT -> if (x > 0) x--
                SDL_SCANCODE_UP -> if (y > 0) y--
                SDL_SCANCODE_RIGHT -> if (x < 200) x++
                SDL_SCANCODE_DOWN -> if (y < 200) y++
            }
        }
    }
    loop.submit(renderTask)
    loop.run()
    renderer.destroy()
    window.destroy()
    KPlatform.destroy()
}

fun mainTetris(args: Array<String>) {
    var startLevel = 0
    var width = 10
    var height = 20
    when (args.size) {
        1 -> startLevel = args[0].toInt()
        2 -> {
            width = args[0].toInt()
            height = args[1].toInt()
        }
        3 -> {
            width = args[0].toInt()
            height = args[1].toInt()
            startLevel = args[2].toInt()
        }
    }
    val visualizer = SDL_Visualizer(width, height)
    val game = Game(width, height, visualizer, visualizer)
    game.startNewGame(startLevel)

    return
}
