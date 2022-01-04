package xyz.mizarc.worldwidewarps.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import xyz.mizarc.worldwidewarps.Config
import xyz.mizarc.worldwidewarps.PlayerContainer
import xyz.mizarc.worldwidewarps.Teleporter
import xyz.mizarc.worldwidewarps.utils.LocationConversions

@CommandAlias("setspawn")
@CommandPermission("worldwidewarps.command.setspawn")
class SetspawnCommand: BaseCommand() {
    @Dependency lateinit var config: Config
    @Dependency lateinit var teleporter: Teleporter
    @Dependency lateinit var playerContainer: PlayerContainer

    @Default
    fun onSetspawn(player: Player) {
        // Teleport the player
        config.setSpawnLocation(player.location)
        player.sendMessage("§aSpawn has been set to your location.")
    }
}