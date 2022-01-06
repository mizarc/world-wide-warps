package xyz.mizarc.worldwidewarps

import org.bukkit.DyeColor
import org.bukkit.OfflinePlayer
import xyz.mizarc.solidclaims.claims.Position
import java.util.*

/**
 * Stores a home consisting of a name and a position.
 * @property id The unique identifier.
 * @property player The player that owns the home.
 * @property name The name of the home.
 * @property colour The colour of the bed.
 * @property position The position in the world.
 */
class Home(val id: UUID, val player: OfflinePlayer, val name: String, val colour: DyeColor, val position: Position)
