package xyz.mizarc.worldwidewarps

import co.aikar.commands.PaperCommandManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.mizarc.worldwidewarps.commands.HomeCommand
import xyz.mizarc.worldwidewarps.events.PlayerRegistrationListener

class WorldWideWarps: JavaPlugin() {
    private lateinit var commandManager: PaperCommandManager
    val playerContainer = PlayerContainer()
    val teleporter = Teleporter(this, playerContainer)

    override fun onEnable() {
        commandManager = PaperCommandManager(this)
        dataFolder.mkdir()
        createConfig()
        registerDependencies()
        registerCommands()
        registerEvents()
        logger.info("WorldWideWarps has been Enabled")
    }

    override fun onDisable() {
        logger.info("WorldWideWarps has been Disabled")
    }

    private fun createConfig() {
        config.addDefault("home_default_to_spawn", true)
        config.addDefault("home_timer", 5)
        config.addDefault("home_cooldown", 30)
        config.addDefault("spawn_timer", 5)
        config.addDefault("spawn_cooldown", 30)
        config.addDefault("invite_range", 20)
        config.addDefault("tpr_radius", 1000)
        config.addDefault("tpr_attempts", 50)
        config.options().copyDefaults(true)
        saveConfig()
    }

    private fun registerDependencies() {
        commandManager.registerDependency(PlayerContainer::class.java, playerContainer)
        commandManager.registerDependency(Teleporter::class.java, teleporter)
    }

    private fun registerCommands() {
        commandManager.registerCommand(HomeCommand())
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerRegistrationListener(playerContainer), this)
    }
}