import ksdl.composition.*
import ksdl.diagnostics.*
import ksdl.events.*
import ksdl.geometry.*
import ksdl.grids.hex.*
import ksdl.rendering.*
import ksdl.resources.*
import ksdl.system.*
import sdl2.*

class HexScene(resources: KResourceContext) : KScene {
    private val layout = HexLayout(listOf(
            KVector(60, -34),
            KVector(60, 34),
            KVector(0, 69),
            KVector(-60, 34),
            KVector(-60, -34),
            KVector(0, -69)
    ))

    private val grass = resources.loadTexture("terrain/grass")
    private val tree = resources.loadImage("terrain/tree")
    private val water = resources.loadTexture("terrain/water")
    private val select = resources.loadTexture("terrain/selected")
    private val hover = resources.loadTexture("terrain/hover")

    private var hoverHex: HexCell = HexCell(0, 0)
    private var selectedHex: HexCell = HexCell(0, 0)

    private var scale = 1.0f
    private var offset = KVector(0, 0)

    private val map = HexMap.buildCircle(20, HexCellDescriptor(HexLandType.Water)).buildLand(listOf(HexCell(0, 0)))

    override fun activate(executor: KTaskExecutor) {

    }

    override fun deactivate(executor: KTaskExecutor) {
    }

    override fun render(renderer: KRenderer) {
        renderer.scale(scale)

        for (cell in map) {
            when (cell.value.type) {
                HexLandType.Water -> renderer.renderHex(cell.key, water)
                HexLandType.Land -> renderer.renderHex(cell.key, grass)
            }
        }

        renderer.renderHex(hoverHex, hover)
        renderer.renderHex(selectedHex, select)
    }

    fun KRenderer.renderHex(hex: HexCell, texture: KTexture) {
        val textureSize = texture.size
        val shift = KVector(-textureSize.width / 2, -textureSize.height / 2)
        val center = layout.cellCenter(hex) + offset + shift
        draw(texture, center)
    }

    override fun event(event: KEvent, executor: KTaskExecutor): Boolean {
        when (event) {
            is KEventKeyDown -> {
                when (event.keyCode.toInt()) {
                    SDLK_UP -> scale += 0.1f
                    SDLK_DOWN -> scale -= 0.1f
                }
            }
            is KEventMouseWheel -> {
                offset += KVector(-event.scrollX, event.scrollY) * 5.0
            }
            is KEventMouseMotion -> {
                hoverHex = layout.cellAt(event.position - offset)
            }
            is KEventMouseDown -> {
                when {
                    event.button == KMouseButton.Left -> selectedHex = hoverHex
                }
                logger.trace((layout.cellCenter(selectedHex) + offset).toString())
            }
        }
        return true
    }
}