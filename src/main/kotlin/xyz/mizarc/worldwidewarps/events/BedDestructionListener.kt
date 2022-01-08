package xyz.mizarc.worldwidewarps.events

import org.bukkit.block.Block
import org.bukkit.block.data.type.Bed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import xyz.mizarc.worldwidewarps.HomeContainer
import xyz.mizarc.worldwidewarps.Position

class BedDestructionListener(val homes: HomeContainer): Listener {

    @EventHandler
    fun onBedDestroy(event: BlockBreakEvent) {
        if (event.block.blockData !is Bed) {
            return
        }

        val bed = event.block.blockData as Bed
        val otherHalf = event.block.getRelative(bed.facing)

        var homesAtPosition = homes.getByPosition(Position(event.block.location))
        if (homesAtPosition.isNotEmpty()) {
            for (home in homesAtPosition) {
                homes.remove(home)
            }
        }

        homesAtPosition = homes.getByPosition(Position(otherHalf.location))
        if (homesAtPosition.isNotEmpty()) {
            for (home in homesAtPosition) {
                homes.remove(home)
            }
        }
    }
}