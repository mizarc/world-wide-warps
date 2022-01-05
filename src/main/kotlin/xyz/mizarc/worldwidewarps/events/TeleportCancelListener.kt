package xyz.mizarc.worldwidewarps.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import xyz.mizarc.worldwidewarps.PlayerContainer

class TeleportCancelListener(val playerContainer: PlayerContainer): Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val playerState = playerContainer.getPlayer(event.player) ?: return

        // Do nothing if player is not teleporting
        val teleportTask = playerState.teleportTask ?: return
        if (!playerState.teleportTask!!.isTeleporting()) return


        // Do nothing if player hasn't moved position
        val xPos = event.from.blockX == event.to!!.blockX
        val yPos = event.from.blockY == event.to!!.blockY
        val zPos = event.from.blockZ == event.to!!.blockZ
        if (xPos && yPos && zPos) {
            return
        }

        teleportTask.cancelTask()
        event.player.sendMessage("§cYou moved. Teleportation has been cancelled.")
    }
}