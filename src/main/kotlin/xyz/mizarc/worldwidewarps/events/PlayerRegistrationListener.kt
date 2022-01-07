package xyz.mizarc.worldwidewarps.events

import net.milkbowl.vault.chat.Chat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.mizarc.worldwidewarps.*

class PlayerRegistrationListener(val homes: HomeContainer, val players: PlayerContainer,
                                 val config: Config, val metadata: Chat): Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val playerState = PlayerState(event.player, config, metadata)
        players.add(playerState)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        players.remove(event.player.uniqueId)
    }
}