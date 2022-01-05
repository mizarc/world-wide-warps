package xyz.mizarc.worldwidewarps

import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList

class PlayerContainer() {
    var playerStates: ArrayList<PlayerState> = ArrayList()

    fun getPlayer(player: Player) : PlayerState? {
        for (playerState in playerStates) {
            if (playerState.player.uniqueId == player.uniqueId) {
                return playerState
            }
        }

        return null
    }

    fun addPlayer(playerState: PlayerState) : Boolean {
        for (existingPlayerState in playerStates) {
            if (existingPlayerState.player.uniqueId == playerState.player.uniqueId) {
                return false
            }
        }
        playerStates.add(playerState)
        return true
    }

    fun removePlayer(playerId: UUID) : Boolean {
        for (playerState in playerStates) {
            if (playerState.player.uniqueId == playerId) {
                playerStates.remove(playerState)
                return true
            }
        }
        return false
    }
}