package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*
import kotlin.collections.ArrayList

class WarpAccessRepository(private val database: Database, private val warpRepository: WarpRepository) {

    fun getByPlayer(player: OfflinePlayer): ArrayList<Warp> {
        val foundWarps = ArrayList<Warp>()

        if (foundWarps.isEmpty()) {
            val results = database.getResults("SELECT warpId FROM warp_access WHERE playerId=?",
                player.uniqueId)

            for (result in results) {
                val warp = warpRepository.getById() ?: continue
                foundWarps.add(warp)
            }
        }
        return foundWarps
    }

    fun getByWarp(warp: Warp): ArrayList<OfflinePlayer> {
        val foundPlayers = ArrayList<OfflinePlayer>()

        val results = database.getResults("SELECT playerId FROM warp_access WHERE warpId=?")
        for (result in results) {
            val player = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId")))
            foundPlayers.add(player)
        }
        return foundPlayers
    }

    fun addWarpForPlayer(player: OfflinePlayer, warp: Warp) {
        database.executeInsert("INSERT INTO warps (id, playerId, name, worldId, " +
                "positionX, positionY, positionZ, direction) VALUES (?, ?, ?)",
            warp.id, warp.player.uniqueId, warp.name, warp.world.uid,
            warp.position.x, warp.position.y, warp.position.z, warp.direction)
    }

    fun removeWarpForPlayer(player: OfflinePlayer, warp: Warp) {
        database.executeUpdate("DELETE FROM warp_access WHERE playerId=? AND warpId=?", warp.id)
    }
}