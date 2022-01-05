package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import org.bukkit.DyeColor
import org.bukkit.entity.Player
import xyz.mizarc.solidclaims.claims.Position
import xyz.mizarc.worldwidewarps.utils.BedColourConversion
import java.sql.SQLException
import java.util.*
import kotlin.collections.ArrayList

class DatabaseStorage {
    private var connection: Database

    init {
        val options = DatabaseOptions.builder().sqlite("homes").build()
        connection = PooledDatabaseOptions.builder().options(options).createHikariDatabase()
        createHomesDatabase()
    }

    /**
     * Fetches a list of homes from the database by player.
     * @param player A reference of the player.
     */
    fun getHomes(player: Player): ArrayList<Home> {
        val homes = ArrayList<Home>()
        return try {
            val results = connection.getResults("SELECT * FROM homes WHERE playerId=?;", player.uniqueId)
            for (result in results) {
                homes.add(Home(
                    UUID.fromString(result.getString("id")), result.getString("name"),
                    DyeColor.valueOf(result.getString("colour")),
                    Position(result.get("positionX"), result.get("positionY"), result.get("positionZ"))))
            }
            homes
        } catch (except: SQLException) {
            homes
        }
    }

    /**
     * Adds as new home assigned to a player.
     * @param player A reference of the player.
     * @param home A reference of the home.
     * @return True if the database query was successful.
     */
    fun addHome(player: Player, home: Home): Boolean {
        return try {
            connection.executeUpdate("INSERT INTO homes (id, playerId, name, colour, positionX, positionY, positionZ) " +
                    "VALUES (?, ?, ?, ?, ?, ?);",
                home.id, player.uniqueId, home.name, home.colour, home.position.x, home.position.y, home.position.z)
            true
        } catch (except: SQLException) {
            false
        }
    }

    /**
     * Removes a home.
     * @param home A reference of the home.
     * @return True if the database query was successful.
     */
    fun removeHome(home: Home): Boolean {
        return try {
            connection.executeUpdate("DELETE FROM homes WHERE id=?;", home.id)
            true
        } catch (except: SQLException) {
            false
        }
    }

    /**
     * Creates the table for homes.
     * @return True if the database query was successful.
     */
    private fun createHomesDatabase(): Boolean {
        return try {
            connection.executeUpdate("CREATE TABLE IF NOT EXISTS homes (id TEXT, playerId TEXT, name TEXT, " +
                    "colour TEXT, positionX INTEGER, positionY INTEGER, positionZ INTEGER);")
            true
        } catch (except: SQLException) {
            false
        }
    }
}
