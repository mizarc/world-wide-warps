package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import org.bukkit.plugin.Plugin
import java.sql.SQLException

class DatabaseStorage(plugin: Plugin) {
    val connection: Database

    init {
        val options = DatabaseOptions.builder().sqlite(plugin.dataFolder.toString() + "/homes.db").build()
        connection = PooledDatabaseOptions.builder().options(options).createHikariDatabase()
        createHomesDatabase()
        createWarpDatabase()
        createWarpAccessDatabase()
    }

    /**
     * Creates the table for homes.
     * @return True if the database query was successful.
     */
    private fun createHomesDatabase(): Boolean {
        return try {
            connection.executeUpdate("CREATE TABLE IF NOT EXISTS homes (id TEXT, playerId TEXT, name TEXT, " +
                    "colour TEXT, worldId TEXT, positionX INTEGER, positionY INTEGER, positionZ INTEGER, " +
                    "direction INT);")
            true
        } catch (except: SQLException) {
            false
        }
    }

    private fun createWarpDatabase(): Boolean {
        return try {
            connection.executeUpdate("CREATE TABLE IF NOT EXISTS warps (id TEXT, playerId TEXT, name TEXT, " +
                    "worldId TEXT, positionX INTEGER, positionY INTEGER, positionZ INTEGER, direction INT);")
            true
        } catch (except: SQLException) {
            false
        }
    }

    private fun createWarpAccessDatabase(): Boolean {
        return try {
            connection.executeUpdate("CREATE TABLE IF NOT EXISTS warps_access (id TEXT, playerId TEXT, " +
                    "warpId TEXT);")
            true
        } catch (except: SQLException) {
            false
        }
    }
}
