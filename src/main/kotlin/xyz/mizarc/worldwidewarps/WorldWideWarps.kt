package xyz.mizarc.worldwidewarps

import co.aikar.commands.PaperCommandManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.mizarc.worldwidewarps.commands.HomeCommand
import xyz.mizarc.worldwidewarps.commands.SetspawnCommand
import xyz.mizarc.worldwidewarps.commands.SpawnCommand
import xyz.mizarc.worldwidewarps.events.PlayerRegistrationListener
import xyz.mizarc.worldwidewarps.events.TeleportCancelListener

class WorldWideWarps: JavaPlugin() {
    private lateinit var commandManager: PaperCommandManager
    private val config = Config(this)
    val playerContainer = PlayerContainer()
    val teleporter = Teleporter(this, playerContainer)

    override fun onEnable() {
        commandManager = PaperCommandManager(this)
        dataFolder.mkdir()
        registerDependencies()
        registerCommands()
        registerEvents()
        logger.info("WorldWideWarps has been Enabled")
    }

    override fun onDisable() {
        logger.info("WorldWideWarps has been Disabled")
    }


    private fun registerDependencies() {
        commandManager.registerDependency(Config::class.java, config)
        commandManager.registerDependency(PlayerContainer::class.java, playerContainer)
        commandManager.registerDependency(Teleporter::class.java, teleporter)
    }

    private fun registerCommands() {
        commandManager.registerCommand(HomeCommand())
        commandManager.registerCommand(SpawnCommand())
        commandManager.registerCommand(SetspawnCommand())
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerRegistrationListener(playerContainer), this)
        server.pluginManager.registerEvents(TeleportCancelListener(playerContainer), this)
    }
}