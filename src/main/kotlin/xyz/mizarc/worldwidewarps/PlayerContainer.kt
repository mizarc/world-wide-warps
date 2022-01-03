package xyz.mizarc.worldwidewarps

import java.util.*
import kotlin.collections.ArrayList

class PlayerContainer() {
    var playerStates: ArrayList<PlayerState> = ArrayList()

    fun getPlayer(playerId: UUID) : PlayerState? {
        for (playerState in playerStates) {
            if (playerState.player.uniqueId == playerId) {
                return playerState
            }
        }

        return null
    }
}