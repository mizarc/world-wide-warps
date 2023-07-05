package xyz.mizarc.worldwidewarps

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import xyz.mizarc.worldwidewarps.utils.LocationConversions

class Teleporter(private val plugin: Plugin, private val config: Config, private val playerRepository: PlayerRepository) {

    fun teleportHome(player: Player): Boolean {
        val homeLocation = player.bedSpawnLocation ?: return false
        val playerState = playerRepository.getByPlayer(player) ?: return false

        // Do cost checks if required
        if (playerState.getHomeTeleportCost() > 1) {
            // Alert player that they can't teleport if they don't meet the cost
            if (!hasCostAmount(player, playerState.getHomeTeleportCost())) {
                player.sendActionBar(Component
                    .text("You require at least ${playerState.getHomeTeleportCost()} ender pearls to teleport home.")
                    .color(TextColor.color(255, 85, 85)))
                return false
            }

            // Teleport player home with cost
            player.sendActionBar(Component
                .text("Teleporting home. This will cost you ${playerState.getHomeTeleportCost()} ender pearls.")
                .color(TextColor.color(85, 255, 255)))
            teleport(player, homeLocation, playerState.getHomeTeleportTimer(),
                TeleportMessage.HOME, playerState.getHomeTeleportCost())
            return true
        }

        // Teleport player home without cost
        teleport(player, homeLocation, playerState.getHomeTeleportTimer())
        return true
    }

    fun teleportSpawn(player: Player): Boolean {
        val spawnLocation = LocationConversions.stringTolocation(config.spawnLocation) ?: return false
        val playerState = playerRepository.getByPlayer(player) ?: return false

        // Do cost checks if required
        if (playerState.getSpawnTeleportCost() > 1) {
            // Alert player that they can't teleport if they don't meet the cost
            if (!hasCostAmount(player, playerState.getHomeTeleportCost())) {
                player.sendActionBar(Component
                    .text("You require at least ${playerState.getSpawnTeleportCost()} ender pearls to teleport to spawn.")
                    .color(TextColor.color(255, 85, 85)))
                return false
            }

            // Teleport player home with cost
            player.sendActionBar(Component
                .text("Teleporting to spawn. This will cost you ${playerState.getSpawnTeleportCost()} ender pearls.")
                .color(TextColor.color(85, 255, 255)))
            teleport(player, spawnLocation, playerState.getSpawnTeleportTimer(),
                TeleportMessage.SPAWN, playerState.getSpawnTeleportCost())
            return true
        }

        // Teleport player home without cost
        teleport(player, spawnLocation, playerState.getSpawnTeleportTimer())
        return true
    }

    fun teleport(player: Player, location: Location, timer: Int = 0,
                 teleportMessage: TeleportMessage = TeleportMessage.NONE, teleportCost: Int = 0): Boolean {
        val playerState = playerRepository.getByPlayer(player) ?: return false

        // Teleports the player instantaneously
        if (timer == 0) {
            removeCostFromInventory(player, teleportCost)
            player.teleport(location)
            return true
        }

        // Teleports the player after a certain amount of time has passed
        playerState.teleportTask = TeleportTask(Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!hasCostAmount(player, teleportCost)) {
                player.sendActionBar(Component
                    .text("Teleport cancelled. You no longer possess the required cost.")
                    .color(TextColor.color(255, 85, 85)))
            }
            else {
                removeCostFromInventory(player, teleportCost)
                player.teleport((location))
                player.sendActionBar(Component
                    .text(teleportMessage.messageString)
                    .color(TextColor.color(85, 255, 85)))
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