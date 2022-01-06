package xyz.mizarc.worldwidewarps

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Teleporter(val plugin: Plugin, val playerContainer: PlayerContainer) {

    fun teleport(player: Player, location: Location, timer: Int = 0,
                 teleportMessage: TeleportMessage = TeleportMessage.NONE): Boolean {
        val playerState = playerContainer.getByPlayer(player) ?: return false
        if (timer == 0) {
            player.teleport(location)
            return true
        }

        playerState.teleportTask = TeleportTask(Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            player.teleport((location))
            playerState.teleportTask!!.cancelTask()
            player.sendMessage(teleportMessage.messageString)
        }, timer * 20L), playerState.player, location)
        return true
    }
}