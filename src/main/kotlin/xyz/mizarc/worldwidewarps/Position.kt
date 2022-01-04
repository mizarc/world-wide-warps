package xyz.mizarc.solidclaims.claims

import org.bukkit.Location

/**
 * Stores three integers to define a position in the world.
 * @property x The X-Axis position.
 * @property y The Y-Axis position.
 * @property z The Z-Axis position.
 */
data class Position(val x: Int, val y: Int, val z: Int) {
    constructor(location: Location): this(location.blockX, location.blockY, location.blockZ)
}