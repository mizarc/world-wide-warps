package xyz.mizarc.worldwidewarps.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Dependency
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import xyz.mizarc.worldwidewarps.Config
import xyz.mizarc.worldwidewarps.PlayerContainer
import xyz.mizarc.worldwidewarps.TeleportMessage
import xyz.mizarc.worldwidewarps.Teleporter

@CommandAlias("home")
@CommandPermission("worldwidewarps.command.home")
class HomeCommand: BaseCommand() {
    @Dependency lateinit var config: Config
    @Dependency lateinit var teleporter: Teleporter
    @Dependency lateinit var playerContainer: PlayerContainer

    @Default
    fun onHome(player: Player) {
        // Get the player's bed location
        val bedSpawnLocation = player.bedSpawnLocation
        if (bedSpawnLocation == null) {
            player.sendMessage("§cYou don't have a home. Sleep in a bed to set your home.")
            return
        }

        // Bypasses the cooldown timer if player has permission
        if (player.hasPermission("worldwidewarps.teleport.home.bypasstimer")) {
            teleporter.teleport(player, bedSpawnLocation, 0)
            player.sendMessage("§aWelcome home.")
            return
        }

        // Teleport the player
        val timer = config.homeTimer
        player.sendMessage("§aTeleporting home, please wait §6$timer §aseconds.")
        teleporter.teleport(player, bedSpawnLocation, timer, TeleportMessage.HOME)
    }
}