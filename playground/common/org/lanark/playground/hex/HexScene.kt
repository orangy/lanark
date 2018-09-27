package org.lanark.playground.hex

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.playground.hex.*
import org.lanark.resources.*
import org.lanark.ui.*

class HexScene(resources: ResourceContext) : Scene {
    private val layout = HexLayout(
        listOf(
            Vector(60, -34),
            Vector(60, 34),
            Vector(0, 69),
            Vector(-60, 34),
            Vector(-60, -34),
            Vector(0, -69)
        )
    )

    private val grass = resources.loadTexture("terrain/grass")
    private val tree = resources.loadImage("terrain/tree")
    private val water = resources.loadTexture("terrain/water")
    private val select = resources.loadTexture("terrain/selected")
    private val hover = resources.loadTexture("terrain/hover")

    private var hoverHex: HexCell = HexCell(0, 0)
    private var selectedHex: HexCell = HexCell(0, 0)

    private var scale = 1.0f
    private var offset = Vector(0, 0)

    private val map = HexMap.buildCircle(20, HexCellDescriptor(HexLandType.Water)).buildLand(listOf(HexCell(0, 0)))

    override fun activate(executor: TaskExecutor) {

    }

    override fun deactivate(executor: TaskExecutor) {
    }

    override fun render(frame: Frame) {
        frame.scale(scale)

        for (cell in map) {
            when (cell.value.type) {
                HexLandType.Water -> frame.renderHex(cell.key, water)
                HexLandType.Land -> frame.renderHex(cell.key, grass)
            }
        }

        frame.renderHex(hoverHex, hover)
        frame.renderHex(selectedHex, select)
    }

    fun Frame.renderHex(hex: HexCell, texture: Texture) {
        val textureSize = texture.size
        val shift = Vector(-textureSize.width / 2, -textureSize.height / 2)
        val center = layout.cellCenter(hex) + offset + shift
        draw(texture, center)
    }

    override fun event(event: Event, executor: TaskExecutor): Boolean {
        when (event) {
            is EventKeyDown -> {
                when (event.scanCode) {
                    82u /* up */ -> scale += 0.1f
                    81u /* down*/ -> scale -= 0.1f
                }
            }
            is EventMouseWheel -> {
                offset += Vector(-event.scrollX, event.scrollY) * 5.0
            }
            is EventMouseMotion -> {
                hoverHex = layout.cellAt(event.position - offset)
            }
            is EventMouseButtonDown -> {
                when {
                    event.button == MouseButton.Left -> selectedHex = hoverHex
                }
                //logger.trace((layout.cellCenter(selectedHex) + offset).toString())
            }
        }
        return true
    }
}