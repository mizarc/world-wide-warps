package dev.mizarc.worldwidewarps

import co.aikar.idb.Database
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.OfflinePlayer
import java.util.*

class HomeRepository(private val database: Database, private val players: PlayerRepository) {
    private val homes: MutableMap<UUID, Home> = mutableMapOf()

    fun init() {
        database.executeUpdate("CREATE TABLE IF NOT EXISTS homes (id TEXT, playerId TEXT, name TEXT, " +
                "colour TEXT, worldId TEXT, positionX INTEGER, positionY INTEGER, positionZ INTEGER, " +
                "direction INT);")

        val results = database.getResults("SELECT * FROM homes;")
        for (result in results) {
            homes[UUID.fromString(result.getString("id"))] = (Home(
                UUID.fromString(result.getString("id")),
                Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                result.getString("name"),
                DyeColor.valueOf(result.getString("colour")),
                Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: continue,
                Position(
                    result.getInt("positionX"),
                    result.getInt("positionY"),
                    result.getInt("positionZ")),
                Direction.values()[result.getInt("direction")]
            ))
        }
    }

    fun getAll(): List<Home> {
        return homes.values.toList()
    }

    fun getByPlayer(player: OfflinePlayer): List<Home> {
        return homes.values.filter { it.player == player }
    }

    fun getByPosition(position: Position): Home? {
        return homes.values.firstOrNull() { it.position == position }
    }

    fun add(home: Home) {
        homes[home.id] = home
        database.executeInsert("INSERT INTO homes (id, playerId, name, colour, worldId, " +
                "positionX, positionY, positionZ, direction) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",
            home.id, home.player.uniqueId, home.name, home.colour, home.world.uid,
            home.position.x, home.position.y, home.position.z, home.direction.ordinal)
    }

    fun update(home: Home) {
        homes.remove(home.id)
        homes[home.id] = home
        database.executeUpdate("UPDATE homes SET playerId=?, name=?, colour=?, worldId=?, " +
                "positionX=?, positionY=?, positionZ=?, direction=? WHERE id=?",
            home.player.uniqueId, home.name, home.colour, home.world.uid,
            home.position.x, home.position.y, home.position.z, home.direction.ordinal, home.id)
        return
    }

    fun remove(home: Home) {
        homes.remove(home.id)
        database.executeUpdate("DELETE FROM homes WHERE id=?", home.id)
    }
}