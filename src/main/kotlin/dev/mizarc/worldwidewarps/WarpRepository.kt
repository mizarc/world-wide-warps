package dev.mizarc.worldwidewarps

import co.aikar.idb.Database
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import java.util.*

class WarpRepository(private val database: Database) {
    private val warps: MutableMap<UUID, Warp> = mutableMapOf()

    fun init() {
        database.executeUpdate("CREATE TABLE IF NOT EXISTS warps (id TEXT, playerId TEXT, name TEXT, " +
                "worldId TEXT, positionX INTEGER, positionY INTEGER, positionZ INTEGER, direction INT, icon TEXT);")

        val results = database.getResults("SELECT * FROM warps;")
        for (result in results) {
            warps[UUID.fromString(result.getString("id"))] = Warp(
                UUID.fromString(result.getString("id")),
                Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                result.getString("name"),
                Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: continue,
                Position(
                    result.getInt("positionX"),
                    result.getInt("positionY"),
                    result.getInt("positionZ")),
                Direction.values()[result.getInt("direction")],
                Material.valueOf(result.getString("icon"))
            )
        }
    }

    fun getAll(): List<Warp> {
        return warps.values.toList()
    }

    fun getById(id: UUID): Warp? {
        return warps.values.firstOrNull { it.id == id }
    }

    fun getByPlayer(player: OfflinePlayer): List<Warp> {
        return warps.values.filter { it.player == player }
    }

    fun getByPosition(position: Position): Warp? {
        return warps.values.firstOrNull { it.position == position }
    }

    fun getByName(name: String): Warp? {
        return warps.values.firstOrNull {it.name == name }
    }

    fun add(warp: Warp) {
        warps[warp.id] = warp
        database.executeInsert("INSERT INTO warps (id, playerId, name, worldId, " +
                "positionX, positionY, positionZ, direction, icon) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",
            warp.id, warp.player.uniqueId, warp.name, warp.world.uid,
            warp.position.x, warp.position.y, warp.position.z, warp.direction.ordinal, warp.icon.name)
    }

    fun update(warp: Warp) {
        warps.remove(warp.id)
        warps[warp.id] = warp
        database.executeUpdate("UPDATE warps SET playerId=?, name=?, worldId=?, " +
                "positionX=?, positionY=?, positionZ=?, direction=?, icon=? WHERE id=?",
            warp.player.uniqueId, warp.name, warp.world.uid, warp.position.x, warp.position.y,
            warp.position.z, warp.direction.ordinal, warp.icon.name, warp.id)
        return
    }

    fun remove(warp: Warp) {
        warps.remove(warp.id)
        database.executeUpdate("DELETE FROM warps WHERE id=?", warp.id)
    }
}