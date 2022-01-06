package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import xyz.mizarc.solidclaims.claims.Position
import java.util.*
import kotlin.collections.ArrayList

class HomeContainer(private val database: Database) {
    private val homes = ArrayList<Home>()

    fun getAll(): ArrayList<Home> {
        val foundHomes = ArrayList<Home>()
        foundHomes.addAll(homes)

        if (foundHomes.isEmpty()) {
            val results = database.getResults("SELECT * FROM homes;")
            for (result in results) {
                foundHomes.add(Home(
                    UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), DyeColor.valueOf(result.getString("colour")),
                    Position(result.getInt("positionX"),
                        result.getInt("positionY"), result.getInt("positionZ"))))
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
            val results = database.getResults("SELECT * FROM homes WHERE playerId=?;", playerState.player.uniqueId)
            for (result in results) {
                foundHomes.add(Home(
                    UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), DyeColor.valueOf(result.getString("colour")),
                    Position(result.getInt("positionX"),
                        result.getInt("positionY"), result.getInt("positionZ"))))
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
            val results = database.getResults("SELECT * FROM homes WHERE position.x AND position.y AND position.z")
            for (result in results) {
                foundHomes.add(Home(
                    UUID.fromString(result.getString("id")),
                    Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerId"))),
                    result.getString("name"), DyeColor.valueOf(result.getString("colour")),
                    Position(result.getInt("positionX"),
                        result.getInt("positionY"), result.getInt("positionZ"))))
            }
        }

        return foundHomes
    }

    fun add(home: Home) {
        homes.add(home)
        database.executeInsert("INSERT INTO homes (id, playerId, name, colour, positionX, positionY, positionZ) "
                + "VALUES (?, ?, ?, ?, ?, ?);")
    }

    fun update(home: Home) {
        for (storedHome in homes) {
            if (storedHome.id == home.id) {
                homes.remove(storedHome)
                homes.add(home)
                break
            }
            return
        }
        database.executeUpdate("UPDATE homes SET name=?, colour=?, positionX=?, positionY=?, positionZ=? "
                + "WHERE id=?")
    }

    fun delete(home: Home) {
        homes.remove(home)
        database.executeUpdate("DELETE FROM homes WHERE id=?", home.id)
    }
}