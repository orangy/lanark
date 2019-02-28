package org.lanark.playground

import org.lanark.resources.*

val logoAssets = scope("logo") {
    image("icon", "lanark-60.png")
    image("icon2x", "lanark-60x2.png")
    texture("logo", "lanark-logo.png")
}

val uiAssets = scope("ui") {
    register(logoAssets)

    font("font", "fonts/good_neighbors.json")
    texture("background", "ui-background.png")
    tiles("elements", "ui-tileset.png") {
        tile("border-top-left", 849, 182, 31, 31, 15, 15)
        tile("border-top", 893, 182, 73, 31, 0, 15)
        tile("border-top-right", 978, 182, 41, 31, 15, 15)
        tile("border-right", 978, 227, 31, 55, 15, 0)
        tile("border-bottom-right", 978, 294, 31, 31, 15, 15)
        tile("border-bottom", 893, 294, 73, 31, 0, 15)
        tile("border-bottom-left", 849, 294, 31, 31, 15, 15)
        tile("border-left", 849, 227, 31, 55, 15, 0)

        tile("button", 11, 124, 290, 60)
        tile("button-pressed", 11, 124 + 78, 290, 60)
        tile("button-hover", 11, 124 + 78 * 2, 290, 60)
        tile("button-disabled", 11, 124 + 78 * 3, 290, 60)
    }
}

val scrollerAssets = scope("scroller") {
    atlas("tiles", "scroller/sheet.json")
    texture("background", "scroller/starfield.png")
}

val gameAssets = scope("main") {
    
    scope("cursors") {
        cursor("normal", "cursor.png", 0, 0)
        cursor("hot", "cursor-outline-red.png", 0, 0)
    }

    scope("terrain") {
        texture("grass", "grass.png")
        texture("tree", "grass-tree.png")
        texture("water", "water.png")

        texture("selected", "tile-select.png")
        texture("hover", "tile-hover.png")
    }
    
    register(uiAssets)
    register(logoAssets)
    register(scrollerAssets)
}