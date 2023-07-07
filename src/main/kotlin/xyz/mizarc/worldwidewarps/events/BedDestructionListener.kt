package xyz.mizarc.worldwidewarps.events

import org.bukkit.block.data.type.Bed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import xyz.mizarc.worldwidewarps.HomeRepository
import xyz.mizarc.worldwidewarps.Position

class                                                                                                                                                                                                                                 BedDestructionListener(val homes: HomeRepository): Listener {

    @EventHandler
    fun onBedDestroy(event: BlockBreakEvent) {
        if (event.block.blockData !is Bed) {
            return
        }

        val bed = event.block.blockData as Bed
        val otherHalf = event.block.getRelative(bed.facing)

        var home = homes.getByPosition(Position(event.block.location))
        if (home != null) {
            homes.remove(home)
        }

        home = homes.getByPosition(Position(otherHalf.location))
        if (home != null) {
            homes.remove(home)
        }
    }
}