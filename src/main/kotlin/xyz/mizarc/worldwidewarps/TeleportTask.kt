package xyz.mizarc.worldwidewarps

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

class TeleportTask(var task: BukkitTask, var player: Player, var location: Location) {

    fun isTeleporting() {
        !task.isCancelled
    }

    fun cancelTask() {
        task.cancel()
    }
}
