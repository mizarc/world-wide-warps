package xyz.mizarc.worldwidewarps.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import org.bukkit.entity.Player
import xyz.mizarc.worldwidewarps.Teleporter
import xyz.mizarc.worldwidewarps.WarpRepository
import xyz.mizarc.worldwidewarps.menus.WarpMenu

@CommandAlias("warpmenu")
@CommandPermission("worldwidewarps.command.warp")
class WarpMenuCommand: BaseCommand() {
    @Dependency lateinit var teleporter: Teleporter
    @Dependency lateinit var warpRepository: WarpRepository

    @Default
    fun onWarp(player: Player, backCommand: String? = null) {
        WarpMenu(warpRepository, teleporter, player).openWarpMenu(backCommand)
    }
}