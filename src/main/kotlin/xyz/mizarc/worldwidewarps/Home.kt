package xyz.mizarc.worldwidewarps

import org.bukkit.DyeColor
import xyz.mizarc.solidclaims.claims.Position
import java.util.*

/**
 * Stores a home consisting of a name and a position.
 * @property name The name of the home.
 * @property position The position in the world.
 */
class Home(val id: UUID, val name: String, val colour: DyeColor, val position: Position)
