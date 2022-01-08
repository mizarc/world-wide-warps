package xyz.mizarc.worldwidewarps

import dev.geco.gsit.objects.IGPoseSeat
import org.bukkit.DyeColor
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.block.data.type.Bed
import org.bukkit.entity.Player
import xyz.mizarc.worldwidewarps.utils.getColour
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
           val position: Position, val direction: Direction) {

    /**
     * Used to create a new home instance with an auto generated UUID.
     * @param player The player that owns the home.
     * @param name The name of the home.
     * @param colour The colour of the bed.
     * @param position The position in the world.
     */
    constructor(player: OfflinePlayer, name: String, colour: DyeColor,
                world: World, position: Position, direction: Direction):
            this(UUID.randomUUID(), player, name, colour, world, position, direction)

    constructor(builder: Builder):
            this(UUID.randomUUID(), builder.player, builder.name, builder.bed.getColour(),
                builder.world, builder.position, builder.direction)

    class Builder(val player: Player, val world: World, val position: Position, val bed: Bed) {
        var name = ""
        var sleep = true
        lateinit var pose: IGPoseSeat
        val direction: Direction = Direction.fromVector(bed.facing.direction)

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun sleep(state: Boolean): Builder {
            this.sleep = state
            return this
        }

        fun pose(pose: IGPoseSeat): Builder {
            this.pose = pose
            return this
        }

        fun build() = Home(this)
    }
}
