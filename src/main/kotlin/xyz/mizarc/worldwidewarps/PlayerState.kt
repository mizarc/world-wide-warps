package xyz.mizarc.worldwidewarps

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class PlayerState(var player: Player) {
    var teleportTask: TeleportTask? = null
    var teleportTimer = 0
    var invitePlayer: Player? = null
}