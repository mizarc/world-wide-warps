package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*
import kotlin.collections.ArrayList

class WarpAccessRepository(private val database: Database, private val warpRepository: WarpRepository) {
    private val player_accesses: MutableMap<UUID, ArrayList<Warp>> = mutableMapOf()
    private val warp_players: MutableMap<UUID, ArrayList<OfflinePlayer>> = mutableMapOf()

    fun init() {
        database.executeUpdate("CREATE TABLE IF NOT EXISTS warp_access (id TEXT, playerId TEXT, warpId TEXT);")

        val results = database.getResults("SELECT * FROM warp_access;")
        for (result in results) {
            val warp = warpRepository.getById(UUID.fromString(result.getString("warpId"))) ?: continue
            val player = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId")))
            player_accesses.getOrPut(player.uniqueId) { arrayListOf() }.add(warp)
            warp_players.getOrPut(warp.id) { arrayListOf() }.add(player)
        }
    }

    fun getByPlayer(player: OfflinePlayer): List<Warp> {
        return player_accesses[player.uniqueId]?.toList() ?: return listOf()
    }

    fun getByWarp(warp: Warp): List<OfflinePlayer> {
        return warp_players[warp.id]?.toList() ?: return listOf()
    }

    fun addWarpForPlayer(player: OfflinePlayer, warp: Warp) {
        player_accesses.getOrPut(player.uniqueId) { arrayListOf() }.add(warp)
        warp_players.getOrPut(warp.id) { arrayListOf() }.add(player)
        database.executeInsert("INSERT INTO warp_access (id, playerId, warpId) VALUES (?, ?, ?)",
            UUID.randomUUID(), player.uniqueId, warp.id)
    }

    fun removeWarpForPlayer(player: OfflinePlayer, warp: Warp) {
        player_accesses[player.uniqueId]?.remove(warp) ?: return
        warp_players[warp.id]?.remove(player) ?: return
        database.executeUpdate("DELETE FROM warp_access WHERE playerId=? AND warpId=?", warp.id)
    }
}