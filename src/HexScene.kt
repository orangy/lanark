import ksdl.composition.*
import ksdl.events.*
import ksdl.grids.hex.*
import ksdl.resources.*
import ksdl.system.*
import sdl2.*

class HexScene(resources: KResourceScope) : KScene {
    private val layout = HexLayout(listOf(
            KVector(60, -34),
            KVector(60, 34),
            KVector(0, 69),
            KVector(-60, 34),
            KVector(-60, -34),
            KVector(0, -69)
    ), origin = KPoint(400, 400))
    private val cornerIndices = listOf(1, 2, 3, 4, 5, 0)

    private val grass = resources.loadImage("terrain/grass")
    private val tree = resources.loadImage("terrain/tree")
    private val select = resources.loadImage("terrain/selected")
    private val hover = resources.loadImage("terrain/hover")

    private var hoverHex: HexCell = HexCell(0, 0)
    private var selectedHex: HexCell = HexCell(0, 0)
    private var scale = 1.0f

    private val map = HexMap.buildCircle(20, CellType.Grass).apply {
        val trees = filter { random() % 10 > 8 }
        trees.forEach { put(it.key, CellType.Tree) }
    }

    enum class CellType {
        Grass, Tree
    }

    override fun activate(executor: KTaskExecutor) {

    }

    override fun deactivate(executor: KTaskExecutor) {
    }

    override fun render(renderer: KRenderer, cache: KTextureCache) {
        val grass = grass.toTexture(cache)
        val tree = tree.toTexture(cache)
        val select = select.toTexture(cache)
        val hover = hover.toTexture(cache)

        renderer.clear(Colors.BLACK)
        renderer.color(Colors.BLUE)
        renderer.scale(scale)

        for (cell in map) {
            when (cell.value) {
                CellType.Grass -> renderer.renderHex(cell.key, grass)
                CellType.Tree -> renderer.renderHex(cell.key, tree)
            }
        }

        renderer.renderHex(hoverHex, hover)
        renderer.renderHex(selectedHex, select)
        renderer.present()
    }

    fun KRenderer.renderHex(hex: HexCell, texture: KTexture) {
        val textureSize = texture.size
        val shift = KVector(-textureSize.width / 2, -textureSize.height / 2)
        val center = layout.cellCenter(hex) + shift
        draw(texture, center)
    }

    fun KRenderer.renderHexOutline(hex: HexCell) {
        val center = layout.cellCenter(hex)
        var previous = center + layout.vectors[0]
        for (index in 0..5) {
            val current = center + layout.vectors[cornerIndices[index]]
            drawLine(previous, current)
            previous = current
        }
    }

    override fun keyboard(event: KEventKey, executor: KTaskExecutor) {
        when (event) {
            is KEventKeyDown -> {
                when (event.keyCode) {
                    SDLK_UP -> scale += 0.1f
                    SDLK_DOWN -> scale -= 0.1f
                }
            }
        }
    }

    override fun mouse(event: KEventMouse, executor: KTaskExecutor) {
        when (event) {
            is KEventMouseMotion -> {
                hoverHex = layout.cellAt(event.position)
            }
            is KEventMouseDown -> {
                when {
                    event.button == KMouseButton.Left -> selectedHex = hoverHex
                }
                logger.trace(layout.cellCenter(selectedHex).toString())
            }
        }
    }
}