package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import java.util.*
import kotlin.collections.ArrayList

class HomeContainer(private val database: Database, private val players: PlayerContainer) {
    private val homes = ArrayList<Home>()

    fun getAll(): ArrayList<Home> {
        val foundHomes = ArrayList<Home>()
        foundHomes.addAll(homes)

        if (foundHomes.isEmpty()) {
            val results = database.getResults("SELECT * FROM homes;")
            for (result in results) {
                val world = Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: continue
                foundHomes.add(Home(
                    UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), DyeColor.valueOf(result.getString("colour")), world,
                    Position(result.getInt("positionX"), result.getInt("positionY"),
                        result.getInt("positionZ")), Direction.values()[result.getInt("direction")]
                ))
            }
        }

        return foundHomes
    }

    fun getByPlayer(playerState: PlayerState): ArrayList<Home> {
        val foundHomes = ArrayList<Home>()
        for (home in homes) {
            if (home.player == Bukkit.getOfflinePlayer(playerState.player.uniqueId)) {
                foundHomes.add(home)
            }
        }

        if (foundHomes.isEmpty()) {
            val results = database.getResults("SELECT * FROM homes WHERE playerId=?;",
                playerState.player.uniqueId)
            for (result in results) {
                val world = Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: continue
                val home = Home(
                    UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), DyeColor.valueOf(result.getString("colour")), world,
                    Position(result.getInt("positionX"), result.getInt("positionY"),
                        result.getInt("positionZ")), Direction.values()[result.getInt("direction")]
                )
                foundHomes.add(home)
                homes.add(home)
            }
        }

        return foundHomes
    }

    fun getByPosition(position: Position): ArrayList<Home> {
        val foundHomes = ArrayList<Home>()
        for (home in homes) {
            if (home.position == position) {
                foundHomes.add(home)
            }
        }

        if (foundHomes.isEmpty()) {
            val results = database.getResults(
                "SELECT * FROM homes WHERE positionX=? AND positionY=? AND positionZ=?",
                position.x, position.y, position.z)
            for (result in results) {
                val world = Bukkit.getWorld(UUID.fromString(result.getString("worldId"))) ?: continue
                foundHomes.add(Home(
                    UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), DyeColor.valueOf(result.getString("colour")), world,
                    Position(result.getInt("positionX"), result.getInt("positionY"),
                        result.getInt("positionZ")), Direction.values()[result.getInt("direction")]
                ))
            }
        }

        return foundHomes
    }

    fun add(home: Home) {
        homes.add(home)
        database.executeInsert("INSERT INTO homes (id, playerId, name, colour, worldId, " +
                "positionX, positionY, positionZ, direction) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",
            home.id, home.player.uniqueId, home.name, home.colour, home.world.uid,
            home.position.x, home.position.y, home.position.z, home.direction.ordinal)
    }

    fun update(home: Home) {
        for (storedHome in getByPlayer(players.getByPlayer(Bukkit.getPlayer(home.player.uniqueId)!!)!!)) {
            println(storedHome.id)
            println(home.id)
            if (storedHome.id == home.id) {
                homes.remove(storedHome)
                homes.add(home)

                database.executeUpdate("UPDATE homes SET playerId=?, name=?, colour=?, worldId=?, " +
                        "positionX=?, positionY=?, positionZ=? direction=? WHERE id=?",
                    home.player.uniqueId, home.name, home.colour, home.world.uid,
                    home.position.x, home.position.y, home.position.z, home.direction.ordinal, home.id)
                return
            }
        }
    }

    fun remove(home: Home) {
        homes.remove(home)
        database.executeUpdate("DELETE FROM homes WHERE id=?", home.id)
    }
}