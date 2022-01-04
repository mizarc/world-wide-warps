package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import org.bukkit.entity.Player
import xyz.mizarc.solidclaims.claims.Position
import java.sql.SQLException

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
            val results = connection.getResults("SELECT * FROM homes WHERE playerId=?", player.uniqueId)
            for (result in results) {
                homes.add(Home(result.get("id"), result.get("name"),
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
            connection.executeUpdate("INSERT INTO homes (id, name, playerId, positionX, positionY, positionZ) " +
                    "VALUES (?, ?, ?, ?, ?, ?)",
                player.uniqueId, home.name, home.position.x, home.position.y, home.position.z)
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
            connection.executeUpdate("DELETE FROM homes WHERE id=?", home.id)
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
            connection.executeUpdate("CREATE TABLE IF NOT EXISTS homes (id TEXT, playerId TEXT, " +
                    "positionX INTEGER, positionY INTEGER, positionZ INTEGER)")
            true
        } catch (except: SQLException) {
            false
        }
    }
}
