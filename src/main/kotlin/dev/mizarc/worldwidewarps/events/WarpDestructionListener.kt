package dev.mizarc.worldwidewarps.events

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import dev.mizarc.worldwidewarps.Position
import dev.mizarc.worldwidewarps.WarpAccessRepository
import dev.mizarc.worldwidewarps.WarpRepository

class WarpDestructionListener(val warpRepository: WarpRepository,
                              val warpAccessRepository: WarpAccessRepository): Listener {
    @EventHandler
    fun onWarpBreak(event: BlockBreakEvent) {
        if (event.block.type != Material.LODESTONE) return

        val existingWarp = warpRepository.getAll().find { it.position == Position(event.block.location) }
        if (existingWarp != null) {
            warpRepository.remove(existingWarp)
            warpAccessRepository.removeAllAccess(existingWarp)
        }
    }
}