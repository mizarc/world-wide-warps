package xyz.mizarc.worldwidewarps

import co.aikar.commands.PaperCommandManager
import org.bukkit.plugin.java.JavaPlugin
import xyz.mizarc.worldwidewarps.commands.HomeCommand
import xyz.mizarc.worldwidewarps.commands.SetspawnCommand
import xyz.mizarc.worldwidewarps.commands.SpawnCommand
import xyz.mizarc.worldwidewarps.events.BedMenuListener
import xyz.mizarc.worldwidewarps.events.PlayerRegistrationListener
import xyz.mizarc.worldwidewarps.events.TeleportCancelListener

class WorldWideWarps: JavaPlugin() {
    private lateinit var commandManager: PaperCommandManager
    private val config = Config(this)
    private val storage = DatabaseStorage()
    val players = PlayerContainer()
    val homes = HomeContainer(storage.connection)
    val teleporter = Teleporter(this, players)


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
        commandManager.registerDependency(DatabaseStorage::class.java, storage)
        commandManager.registerDependency(PlayerContainer::class.java, players)
        commandManager.registerDependency(Teleporter::class.java, teleporter)
    }

    private fun registerCommands() {
        commandManager.registerCommand(HomeCommand())
        commandManager.registerCommand(SpawnCommand())
        commandManager.registerCommand(SetspawnCommand())
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerRegistrationListener(homes, players), this)
        server.pluginManager.registerEvents(TeleportCancelListener(players), this)
        server.pluginManager.registerEvents(BedMenuListener(homes, players), this)
    }
}