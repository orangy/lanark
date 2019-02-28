package org.lanark.playground

import kotlinx.coroutines.*
import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.math.*
import org.lanark.resources.*
import org.lanark.ui.*
import kotlin.random.*

class ScrollerScene(
    private val application: SceneApplication,
    private val resources: ResourceContext
) : Scene {

    val logger = application.frame.engine.logger
    private val coroutineScope = application.frame.engine.createCoroutineScope()

    private val background = resources.texture("background")
    private val tiles = resources.tiles("tiles")
    private val playerTile = tiles["playerShip1_blue.png"]
    private val bulletTile = tiles["laserRed03.png"]
    private val enemyTiles = listOf("Red", "Blue", "Green", "Black")
        .flatMap { color -> (1..5).map { "enemy$color$it.png" } }
        .map { tiles[it] }


    private val playerControls = mutableMapOf<PlayerControls, Boolean>()

    private var backgroundOffset = 0f
    private var playerPosition = (application.frame.width / 2 - playerTile.width / 2).toFloat()
    private var playerSpeed = 300f

    private var bullets = listOf<Bullet>()
    private var bulletSpeed = 400f
    private var timeTillNextBullet = 0f
    private var bulletRate = 0.3f

    private var enemies = listOf<Enemy>()
    private var enemySpeed = 50f
    private var timeTillNextEnemy = 10f
    private var enemyRate = 5f

    private enum class PlayerControls {
        Left, Right, Fire
    }

    private data class Bullet(var x: Float, var y: Float, val speed: Vector2)
    private data class Enemy(var x: Float, var y: Float, val speed: Vector2, val tile: Tile)

    override fun activate(frame: Frame) {
        coroutineScope.launch {
            while (true) {
                val dt = frame.engine.nextTick()

                frame.updateBackground(dt)
                frame.updatePlayer(dt)
                frame.updateBullets(dt)
                frame.updateEnemies(dt)
            }
        }
    }

    override fun deactivate(frame: Frame) {
        coroutineScope.coroutineContext.cancel()
    }

    override fun render(frame: Frame) {
        frame.renderBackground()
        frame.renderEnemies()
        frame.renderBullets()
        frame.renderPlayer()
    }

    override fun event(frame: Frame, event: Event): Boolean {
        when (event) {
            is EventKey -> when (event.scanCode) {
                80u, 123u -> playerControls[PlayerControls.Left] = event is EventKeyDown
                79u, 124u -> playerControls[PlayerControls.Right] = event is EventKeyDown
                44u, 49u -> playerControls[PlayerControls.Fire] = event is EventKeyDown
            }
        }
        return false
    }

    private fun Frame.updateBackground(dt: Float) {
        backgroundOffset += dt * 100
    }

    private fun Frame.renderBackground() {
        val (x, y, w, h) = rectangle
        val splitY = backgroundOffset.toInt() % h
        fill(background, Rect(x, y + splitY - h, w, h))
        fill(background, Rect(x, y + splitY, w, h))
    }

    private fun Frame.updateEnemies(dt: Float) {
        enemies.forEach {
            it.x += dt * it.speed.x
            it.y += dt * it.speed.y
        }

        enemies = enemies.filter { Point(it.x.toInt(), it.y.toInt()) in rectangle.expand(it.tile.width, it.tile.height) }
        enemies = enemies.filter { enemy->
            val enemyRect = Rect(Point(enemy.x.toInt(), enemy.y.toInt()), enemy.tile.size)
            bullets.none { bullet->
                val pos = Point(bullet.x.toInt(), bullet.y.toInt())
                pos in enemyRect
            }
        }

        if (timeTillNextEnemy <= 0f) {
            val tile = enemyTiles[Random.nextInt(enemyTiles.size)]
            enemies += Enemy(
                Random.nextFloat(0f, (width - tile.width).toFloat()),
                -tile.height.toFloat(),
                vectorOf(0f, enemySpeed),
                tile
            )
            timeTillNextEnemy = enemyRate
        } else if (timeTillNextEnemy > 0f) {
            timeTillNextEnemy -= dt
        }

    }

    private fun Frame.renderEnemies() {
        enemies.forEach {
            draw(it.tile, Point(it.x.toInt(), it.y.toInt()))
        }
    }

    private fun Frame.updateBullets(dt: Float) {
        bullets.forEach {
            it.x += dt * it.speed.x
            it.y += dt * it.speed.y
        }

        bullets = bullets.filter { Point(it.x.toInt(), it.y.toInt()) in rectangle }

        if (timeTillNextBullet <= 0f) {
            if (playerControls[PlayerControls.Fire] == true) {
                bullets += Bullet(
                    playerPosition + playerTile.width / 2 - bulletTile.width / 2,
                    (size.height - playerTile.height - bulletTile.height - 32).toFloat(),
                    vectorOf(0f, -bulletSpeed)
                )
                timeTillNextBullet = bulletRate
            }
        } else if (timeTillNextBullet > 0f) {
            timeTillNextBullet -= dt
        }
    }

    private fun Frame.renderBullets() {
        bullets.forEach {
            draw(bulletTile, Point(it.x.toInt(), it.y.toInt()))
        }
    }

    private fun Frame.updatePlayer(dt: Float) {
        var playerControl = 0f

        if (playerControls[PlayerControls.Left] == true)
            playerControl -= 1f
        if (playerControls[PlayerControls.Right] == true)
            playerControl += 1f

        val newPlayerPosition = playerPosition + dt * playerSpeed * playerControl
        if (newPlayerPosition in 0f..(width - playerTile.width).toFloat()) {
            playerPosition = newPlayerPosition
        }
    }

    private fun Frame.renderPlayer() {
        draw(playerTile, Point(playerPosition.toInt(), size.height - playerTile.height - 32))
    }
}

private fun Rect.expand(dx: Int, dy: Int): Rect = Rect(x - dx, y - dy, width + dx * 2, height + dy * 2)
