package xyz.mizarc.worldwidewarps

import org.bukkit.DyeColor
import org.bukkit.OfflinePlayer
import org.bukkit.World
import java.util.*

/**
 * Stores a home consisting of a name and a position.
 * @property id The unique identifier.
 * @property player The player that owns the home.
 * @property name The name of the home.
 * @property colour The colour of the bed.
 * @property position The position in the world.
 */
class Home(val id: UUID, val player: OfflinePlayer, val name: String, val colour: DyeColor, val world: World,
           val position: Position
) {

    /**
     * Used to create a new home instance with an auto generated UUID.
     * @param player The player that owns the home.
     * @param name The name of the home.
     * @param colour The colour of the bed.
     * @param position The position in the world.
     */
    constructor(player: OfflinePlayer, name: String, colour: DyeColor, world: World, position: Position):
            this(UUID.randomUUID(), player, name, colour, world, position)
}
