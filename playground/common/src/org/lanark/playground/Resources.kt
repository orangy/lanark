package org.lanark.playground

import org.lanark.resources.*

val logoAssets = scope("logo") {
    image("icon", "lanark-60.png")
    image("icon2x", "lanark-60x2.png")
    texture("logo", "lanark-logo.png")
}

val uiAssets = scope("ui") {
    register(logoAssets)

    texture("background", "ui-background.png")
    tiles("elements", "ui-tileset.png") {
        tile("border-top-left", 855, 188, 24, 24, 14, 14)
        tile("border-top", 893, 188, 72, 24, 0, 14)
        tile("border-top-right", 978, 188, 24, 24, 11, 14)
        tile("border-right", 978, 228, 24, 54, 11, 0)
        tile("border-bottom-right", 978, 294, 24, 24, 11, 11)
        tile("border-bottom", 893, 294, 72, 24, 0, 11)
        tile("border-bottom-left", 855, 294, 24, 24, 14, 11)
        tile("border-left", 855, 228, 24, 54, 14, 0)

        tile("button", 12, 126, 285, 54)
        tile("button-pressed", 12, 126 + 78, 285, 54)
        tile("button-hover", 12, 126 + 78 * 2, 285, 54)
        tile("button-disabled", 12, 126 + 78 * 3, 285, 54)
    }
}

val gameAssets = scope("org.lanark.playground.main") {
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

    scope("welcome") {
        texture("background-image", "welcome-background.png")
        //music("background-music", "welcome-music.ogg")

        texture("item", "object.png")
    }

    register(uiAssets)
    register(logoAssets)
}