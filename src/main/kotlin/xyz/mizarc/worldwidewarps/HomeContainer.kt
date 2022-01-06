package xyz.mizarc.worldwidewarps

import co.aikar.idb.Database
import org.bukkit.DyeColor
import xyz.mizarc.solidclaims.claims.Position
import java.util.*
import kotlin.collections.ArrayList

class HomeContainer(private val database: Database, playerState: PlayerState) {
    private val homes = ArrayList<Home>()

    init {
        val results = database.getResults("SELECT * FROM homes WHERE playerId=?;", playerState.player.uniqueId)
        for (result in results) {
            homes.add(Home(
                UUID.fromString(result.getString("id")), result.getString("name"),
                DyeColor.valueOf(result.getString("colour")),
                Position(result.getInt("positionX"),
                    result.getInt("positionY"), result.getInt("positionZ"))))
        }
    }

    fun getAll(): ArrayList<Home> {
        return homes
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