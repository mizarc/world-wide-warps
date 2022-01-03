package xyz.mizarc.worldwidewarps

import co.aikar.commands.PaperCommandManager
import org.bukkit.plugin.java.JavaPlugin
import xyz.mizarc.worldwidewarps.commands.HomeCommand

class WorldWideWarps: JavaPlugin() {
    private lateinit var commandManager: PaperCommandManager

    override fun onEnable() {
        commandManager = PaperCommandManager(this)
        registerCommands()
        logger.info("WorldWideWarps has been Enabled")
    }

    override fun onDisable() {
        logger.info("WorldWideWarps has been Disabled")
    }

    private fun registerCommands() {
        commandManager.registerCommand(HomeCommand())
    }
}