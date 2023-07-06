package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import org.bukkit.Bukkit
import java.util.*
import kotlin.collections.ArrayList

class WarpRepository(private val database: Database, private val players: PlayerRepository) {
    private val warps = ArrayList<Warp>()

    fun getAll(): ArrayList<Warp> {
        val foundWarps = ArrayList<Warp>()
        foundWarps.addAll(warps)

        if (foundWarps.isEmpty()) {
            val results = database.getResults("SELECT * FROM warps;")
            for (result in results) {
                val world = Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: continue
                foundWarps.add(Warp(
                    UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), world,
                    Position(result.getInt("positionX"), result.getInt("positionY"),
                        result.getInt("positionZ")), Direction.values()[result.getInt("direction")]))
            }
        }
        return foundWarps
    }

    fun getById(): Warp? {
            val result = database.getFirstRow("SELECT * FROM warps WHERE id=?;")
            val world = Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: return null
            return Warp(UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), world,
                    Position(result.getInt("positionX"), result.getInt("positionY"),
                        result.getInt("positionZ")), Direction.values()[result.getInt("direction")])
    }

    fun getByPlayer(playerState: PlayerState): ArrayList<Warp> {
        val foundWarps = ArrayList<Warp>()
        for (warp in warps) {
            if (warp.player == Bukkit.getOfflinePlayer(playerState.player.uniqueId)) {
                foundWarps.add(warp)
            }
        }

        if (foundWarps.isEmpty()) {
            val results = database.getResults("SELECT * FROM warps WHERE playerId=?;",
                playerState.player.uniqueId)

            for (result in results) {
                val world = Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: continue
                val warp = Warp(UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), world, Position(result.getInt("positionX"),
                        result.getInt("positionY"), result.getInt("positionZ")),
                        Direction.values()[result.getInt("direction")])
                foundWarps.add(warp)
                warps.add(warp)
            }
        }
        return foundWarps
    }

    fun getByPosition(position: Position): ArrayList<Warp> {
        val foundWarps = ArrayList<Warp>()
        for (warp in warps) {
            if (warp.position == position) {
                foundWarps.add(warp)
            }
        }

        if (foundWarps.isEmpty()) {
            val results = database.getResults(
                "SELECT * FROM homes WHERE positionX=? AND positionY=? AND positionZ=?",
                position.x, position.y, position.z)

            for (result in results) {
                val world = Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: continue
                foundWarps.add(Warp(UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), world, Position(result.getInt("positionX"),
                        result.getInt("positionY"), result.getInt("positionZ")),
                    Direction.values()[result.getInt("direction")]))
            }
        }
        return foundWarps
    }

    fun add(warp: Warp) {
        warps.add(warp)
        database.executeInsert("INSERT INTO warps (id, playerId, name, worldId, " +
                "positionX, positionY, positionZ, direction) VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
            warp.id, warp.player.uniqueId, warp.name, warp.world.uid,
            warp.position.x, warp.position.y, warp.position.z, warp.direction)
    }

    fun update(warp: Warp) {
        for (storedWarp in getByPlayer(players.getByPlayer(Bukkit.getPlayer(warp.player.uniqueId)!!)!!)) {
            if (storedWarp.id == warp.id) {
                warps.remove(storedWarp)
                warps.add(warp)

                database.executeUpdate("UPDATE warps SET playerId=?, name=?, worldId=?, " +
                        "positionX=?, positionY=?, positionZ=?, direction=? WHERE id=?",
                    warp.player.uniqueId, warp.name, warp.world.uid, warp.position.x, warp.position.y,
                    warp.position.z, warp.direction.ordinal, warp.id)
                return
            }
        }
    }

    fun remove(warp: Warp) {
        warps.remove(warp)
        database.executeUpdate("DELETE FROM warps WHERE id=?", warp.id)
    }
}