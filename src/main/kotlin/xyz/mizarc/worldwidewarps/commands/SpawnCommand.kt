package xyz.mizarc.worldwidewarps.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Dependency
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import xyz.mizarc.worldwidewarps.Config
import xyz.mizarc.worldwidewarps.TeleportMessage
import xyz.mizarc.worldwidewarps.Teleporter
import xyz.mizarc.worldwidewarps.utils.LocationConversions

@CommandAlias("spawn")
@CommandPermission("worldwidewarps.command.spawn")
class SpawnCommand: BaseCommand() {
    @Dependency lateinit var config: Config
    @Dependency lateinit var teleporter: Teleporter

    @Default
    fun onSpawn(player: Player) {
        // Cancel if spawn doesn't exist
        val spawnLocationString = config.spawnLocation
        if (spawnLocationString.isBlank()) {
            player.sendMessage("§cThere is no set spawn on this server.")
            return
        }

        // Cancel if set spawn location is invalid
        val spawnLocation = LocationConversions.stringTolocation(spawnLocationString)
        if (spawnLocation == null) {
            player.sendMessage("§cThe spawn is invalid. Ask an administrator to fix this.")
            return
        }

        // Instant teleport if player if bypassing the timer
        if (player.hasPermission("worldwidewarps.teleport.spawn.bypasstimer")) {
            teleporter.teleport(player, spawnLocation, 0, TeleportMessage.HOME)
            return
        }

        // Teleports the player with timer
        val timer = config.spawnTimer
        player.sendMessage("§aTeleporting to spawn, please wait §6$timer §aseconds.")
        teleporter.teleport(player, spawnLocation, timer, TeleportMessage.HOME)
    }
}