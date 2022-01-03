package xyz.mizarc.worldwidewarps.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Dependency
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import xyz.mizarc.worldwidewarps.TeleportMessage
import xyz.mizarc.worldwidewarps.Teleporter

@CommandAlias("home")
@CommandPermission("worldwidewarps.command.home")
class HomeCommand: BaseCommand() {
    @Dependency lateinit var config: FileConfiguration
    @Dependency lateinit var teleporter: Teleporter

    @Default
    fun onHome(player: Player) {
        val bedSpawnLocation = player.bedSpawnLocation
        if (bedSpawnLocation == null) {
            player.sendMessage("§cYou don't have a home. Sleep in a bed to set your home.")
            return
        }

        val timer = config.getInt("home_timer")
        player.sendMessage("§aTeleporting home, please wait §6$timer §aseconds.")
        teleporter.teleport(player, bedSpawnLocation, timer, TeleportMessage.HOME)
    }
}