import kdsl.KEventLoop
import ksdl.*

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
    KGraphics.init {
        logger = KLoggerConsole()
        enableEverything()
    }

    val window = KGraphics.createWindow("Kotlin SDL2 Demo", 100, 100, 800, 600)
    window.setBordered(true)
    val renderer = window.renderer()
    val sfc = KGraphics.createSurface(KSize(200, 200), 32)
    sfc.fill(Colors.BLUE)
    val tx2 = sfc.toTexture(renderer)
    val texture = renderer.loadTexture("tetris_all.bmp")
    val loop = KEventLoop()
    var x = 20
    var y = 20
    val renderTask = {
        renderer.clear(Colors.BLACK)
        renderer.draw(tx2, KRect(x, y, 50, 50))
        renderer.present()
    }
    val moveTask = {
        if (x < 200) {
            x++
            loop.submit(renderTask)
            loop.submitSelf()
        }
    }
    loop.submit(renderTask)
    loop.submit(moveTask)
    loop.run()
    renderer.destroy()
    window.destroy()
    KGraphics.destroy()
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
