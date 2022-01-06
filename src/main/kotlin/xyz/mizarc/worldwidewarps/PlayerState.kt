package xyz.mizarc.worldwidewarps

import org.bukkit.entity.Player

class PlayerState(val homes: HomeContainer, val player: Player) {
    var teleportTask: TeleportTask? = null
    var teleportCooldownTimer = 0
    var invitePlayer: Player? = null
}