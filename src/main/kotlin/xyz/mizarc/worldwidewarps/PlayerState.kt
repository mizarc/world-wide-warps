package xyz.mizarc.worldwidewarps

import net.milkbowl.vault.chat.Chat
import org.bukkit.entity.Player

class PlayerState(val player: Player, val config: Config, val metadata: Chat) {
    var teleportTask: TeleportTask? = null
    var teleportCooldownTimer = 0
    var invitePlayer: Player? = null
    var isLaying: Boolean = false
    var inBedMenu: Boolean = false

    fun getHomeLimit(): Int {
        if (metadata.getPlayerInfoInteger(player, "home_limit", -1) > -1) {
            return metadata.getPlayerInfoInteger(player, "home_limit", -1)
        }
        return config.homeLimit
    }

    fun getHomeTeleportTimer(): Int {
        if (metadata.getPlayerInfoInteger(player, "home_teleport_timer", -1) > -1) {
            return metadata.getPlayerInfoInteger(player, "home_teleport_timer", -1)
        }
        return config.homeTimer
    }

    fun getHomeTeleportCost(): Int {
        if (metadata.getPlayerInfoInteger(player, "home_teleport_cost", -1) > -1) {
            return metadata.getPlayerInfoInteger(player, "home_teleport_cost", -1)
        }
        return config.homeTimer
    }

    fun getSpawnTeleportTimer(): Int {
        if (metadata.getPlayerInfoInteger(player, "spawn_teleport_timer", -1) > -1) {
            return metadata.getPlayerInfoInteger(player, "spawn_teleport_timer", -1)
        }
        return config.homeTimer
    }

    fun getSpawnTeleportCost(): Int {
        if (metadata.getPlayerInfoInteger(player, "spawn_teleport_cost", -1) > -1) {
            return metadata.getPlayerInfoInteger(player, "spawn_teleport_cost", -1)
        }
        return config.homeTimer
    }
}