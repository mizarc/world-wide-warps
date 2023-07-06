package xyz.mizarc.worldwidewarps.events

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import xyz.mizarc.worldwidewarps.*
import xyz.mizarc.worldwidewarps.menus.WarpManagementMenu

class WarpInteractListener(var warpRepository: WarpRepository): Listener {

    @EventHandler
    fun onPlayerWarpInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if ((event.clickedBlock?.type ?: return) != Material.LODESTONE) return

        val existingWarp = warpRepository.getAll().find { it.position == Position(event.interactionPoint!!) }
        if (event.player.isSneaking) {
            val warpBuilder = Warp.Builder(event.player, event.clickedBlock!!.location.world, Position(event.clickedBlock!!.location))
            WarpManagementMenu(warpRepository, warpBuilder).openWarpManagementMenu()
        }

        if (existingWarp == null) return
    }
}