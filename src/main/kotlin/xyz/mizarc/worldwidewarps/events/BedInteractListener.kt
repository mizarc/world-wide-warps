package xyz.mizarc.worldwidewarps.events

import dev.geco.gsit.api.GSitAPI
import org.bukkit.block.data.type.Bed
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import xyz.mizarc.worldwidewarps.*
import xyz.mizarc.worldwidewarps.menus.BedMenu

class BedInteractListener(private val homes: HomeContainer, private val players: PlayerContainer): Listener {

    @EventHandler
    fun onBedShiftClick(event: PlayerBedEnterEvent) {
        if (!event.player.hasPermission("worldwidewarps.action.multihome")
            || !event.player.isSneaking || event.bed.blockData !is Bed) {
            return
        }

        val playerState = players.getByPlayer(event.player) ?: return
        val bed = event.bed.blockData as Bed
        val direction = Direction.fromVector(bed.facing.direction)
        val pose = GSitAPI.createPose(event.bed, event.player, Pose.SLEEPING, 0.0,
            0.0, 0.0, Direction.toYaw(direction), true, false)
        val homeBuilder = Home.Builder(event.player, event.bed.world, Position(event.bed.location), bed)
            .pose(pose).sleep(false)

        event.player.bedSpawnLocation = event.bed.location
        event.isCancelled = true
        BedMenu(homes, playerState, homeBuilder).openHomeSelectionMenu()
    }
}