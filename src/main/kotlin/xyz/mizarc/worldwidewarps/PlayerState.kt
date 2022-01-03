package xyz.mizarc.worldwidewarps

import org.bukkit.entity.Player

class PlayerState(var player: Player) {
    var teleportTask: TeleportTask? = null
    var teleportCooldownTimer = 0
    var invitePlayer: Player? = null
}