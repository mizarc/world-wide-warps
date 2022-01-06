package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import java.sql.SQLException

class DatabaseStorage {
    val connection: Database

    init {
        val options = DatabaseOptions.builder().sqlite("homes").build()
        connection = PooledDatabaseOptions.builder().options(options).createHikariDatabase()
        createHomesDatabase()
    }

    /**
     * Creates the table for homes.
     * @return True if the database query was successful.
     */
    private fun createHomesDatabase(): Boolean {
        return try {
            connection.executeUpdate("CREATE TABLE IF NOT EXISTS homes (id TEXT, playerId TEXT, name TEXT, " +
                    "colour TEXT, world TEXT, positionX INTEGER, positionY INTEGER, positionZ INTEGER);")
            true
        } catch (except: SQLException) {
            false
        }
    }
}
