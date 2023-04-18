package xyz.mizarc.worldwidewarps.events

import dev.geco.gsit.api.GSitAPI
import dev.geco.gsit.api.event.PrePlayerGetUpPoseEvent
import dev.geco.gsit.objects.GetUpReason
import org.bukkit.block.data.type.Bed
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
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

        event.isCancelled = true

        // Set player's view to align with the bed
        val bed = event.bed.blockData as Bed
        val direction = Direction.fromVector(bed.facing.direction)
        val newLocation = event.bed.location
        newLocation.pitch = 0.0f
        newLocation.yaw = Direction.toYaw(direction)
        event.player.teleport(newLocation)

        // Set player's pose to look like they're sleeping
        GSitAPI.createPose(event.bed, event.player, Pose.SLEEPING, 0.0,
            0.0, 0.0, Direction.toYaw(direction), true)
        event.player.bedSpawnLocation = event.bed.location

        // Create menu
        val homeBuilder = Home.Builder(event.player, event.bed.world, Position(event.bed.location), bed)
        val playerState = players.getByPlayer(event.player) ?: return
        BedMenu(homes, playerState, homeBuilder).openHomeSelectionMenu()
    }

    @EventHandler
    fun onPlayerGetUpPoseEvent(event: PrePlayerGetUpPoseEvent) {
        val playerState = players.getByPlayer(event.player) ?: return
        if (playerState.inBedMenu) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBedMenuCloseEvent(event: InventoryCloseEvent) {
        val playerState = players.getByPlayer(event.player as Player) ?: return
        if (playerState.inBedMenu) {
            playerState.inBedMenu = false
            GSitAPI.removePose(event.player as Player, GetUpReason.GET_UP)
        }
    }
}