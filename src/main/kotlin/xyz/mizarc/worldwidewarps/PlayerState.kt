package xyz.mizarc.worldwidewarps

import org.bukkit.entity.Player

class PlayerState(val player: Player, val homes: Array<Home>) {
    var teleportTask: TeleportTask? = null
    var teleportCooldownTimer = 0
    var invitePlayer: Player? = null
}