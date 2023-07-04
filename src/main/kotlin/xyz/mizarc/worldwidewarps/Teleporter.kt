package xyz.mizarc.worldwidewarps

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Teleporter(private val plugin: Plugin, private val playerContainer: PlayerContainer) {

    fun teleportHome(player: Player): Boolean {
        val homeLocation = player.bedSpawnLocation ?: return false
        val playerState = playerContainer.getByPlayer(player) ?: return false

        // Do cost checks if required
        if (playerState.getHomeTeleportCost() > 1) {
            // Alert player that they can't teleport if they don't meet the cost
            if (!hasCostAmount(player, playerState.getHomeTeleportCost())) {
                player.sendPlainMessage("Cannot teleport. " +
                        "You require at least ${playerState.getHomeTeleportCost()} ender pearls to teleport.")
                return false
            }

            // Teleport player home with cost
            player.sendPlainMessage("Teleporting home." +
                    "This will cost you ${playerState.getHomeTeleportCost()} ender pearls.")
            teleport(player, homeLocation, playerState.getHomeTeleportTimer(),
                TeleportMessage.HOME, playerState.getHomeTeleportCost())
            return true
        }

        // Teleport player home without cost
        teleport(player, homeLocation, playerState.getHomeTeleportTimer())
        return true
    }

    fun teleport(player: Player, location: Location, timer: Int = 0,
                 teleportMessage: TeleportMessage = TeleportMessage.NONE, teleportCost: Int = 0): Boolean {
        val playerState = playerContainer.getByPlayer(player) ?: return false

        // Teleports the player instantaneously
        if (timer == 0) {
            removeCostFromInventory(player, teleportCost)
            player.teleport(location)
            return true
        }

        // Teleports the player after a certain amount of time has passed
        playerState.teleportTask = TeleportTask(Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!hasCostAmount(player, teleportCost)) {
                player.sendPlainMessage("Teleport cancelled. You no longer possess the required cost.")
            }
            else {
                removeCostFromInventory(player, teleportCost)
                player.teleport((location))
                player.sendPlainMessage(teleportMessage.messageString)
            }
            playerState.teleportTask?.cancelTask()
        }, timer * 20L), playerState.player, location)
        return true
    }

    private fun hasCostAmount(player: Player, teleportCost: Int): Boolean {
        var count = 0
        for (item in player.inventory.contents!!) {
            if (item != null && item.type == Material.ENDER_PEARL) {
                count += item.amount
                if (count >= teleportCost) {
                    return true
                }
            }
        }
        return false
    }

    private fun removeCostFromInventory(player: Player, teleportCost: Int) {
        var count = teleportCost
        for (item in player.inventory.contents!!) {
            if (item != null && item.type == Material.ENDER_PEARL) {
                val remaining = player.inventory.removeItem(ItemStack(Material.ENDER_PEARL, teleportCost))
                count -= remaining[0]?.amount ?: 5
                if (count <= 0) {
                    return
                }
            }
        }
    }
}