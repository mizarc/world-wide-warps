package xyz.mizarc.worldwidewarps.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.mizarc.worldwidewarps.DatabaseStorage
import xyz.mizarc.worldwidewarps.HomeContainer
import xyz.mizarc.worldwidewarps.PlayerContainer
import xyz.mizarc.worldwidewarps.PlayerState

class PlayerRegistrationListener(val homes: HomeContainer, val players: PlayerContainer): Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val playerState = PlayerState(event.player)
        players.add(playerState)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        players.remove(event.player.uniqueId)
    }
}