package xyz.mizarc.worldwidewarps.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import xyz.mizarc.worldwidewarps.PlayerContainer

class TeleportCancelListener(val playerContainer: PlayerContainer): Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val playerState = playerContainer.getPlayer(event.player.uniqueId) ?: return

        // Do nothing if player is not teleporting
        val teleportTask = playerState.teleportTask ?: return
        if (!playerState.teleportTask!!.isTeleporting()) return


        // Do nothing if player hasn't moved position
        val x = event.from.blockX == event.to!!.blockX
        val y = event.from.blockY == event.to!!.blockY
        val z = event.from.blockZ == event.to!!.blockZ
        if (x && y && z) {
            return
        }

        teleportTask.cancelTask()
        event.player.sendMessage("You moved. Teleportation has been cancelled.")
    }
}