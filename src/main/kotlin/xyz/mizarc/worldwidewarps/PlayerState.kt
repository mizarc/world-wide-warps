package xyz.mizarc.worldwidewarps

import net.milkbowl.vault.chat.Chat
import org.bukkit.entity.Player

class PlayerState(val player: Player, val config: Config, val metadata: Chat) {
    var teleportTask: TeleportTask? = null
    var teleportCooldownTimer = 0
    var invitePlayer: Player? = null

    fun getHomeLimit(): Int {
        if (metadata.getPlayerInfoInteger(player, "home_limit", -1) > -1) {
            return metadata.getPlayerInfoInteger(player, "home_limit", -1)
        }
        return config.homeLimit
    }
}