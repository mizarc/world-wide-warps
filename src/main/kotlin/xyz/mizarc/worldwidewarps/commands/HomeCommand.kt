package xyz.mizarc.worldwidewarps.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player

@CommandAlias("home")
@CommandPermission("worldwidewarps.command.home")
class HomeCommand: BaseCommand() {

    @Default
    fun onHome(player: Player) {
        val bedSpawnLocation = player.bedSpawnLocation
        if (bedSpawnLocation == null) {
            player.sendMessage("You don't have a home. Sleep in a bed to set your home.")
            return
        }

        player.teleport(bedSpawnLocation)
    }
}