package xyz.mizarc.worldwidewarps.events

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.type.Bed
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.inventory.ItemStack
import xyz.mizarc.worldwidewarps.Position
import xyz.mizarc.worldwidewarps.Home
import xyz.mizarc.worldwidewarps.HomeContainer
import xyz.mizarc.worldwidewarps.PlayerContainer
import xyz.mizarc.worldwidewarps.utils.getColour
import xyz.mizarc.worldwidewarps.utils.lore
import xyz.mizarc.worldwidewarps.utils.name
import xyz.mizarc.worldwidewarps.utils.toBed

class BedMenuListener(private val homes: HomeContainer, private val players: PlayerContainer): Listener {

    @EventHandler
    fun onBedShiftClick(event: PlayerBedEnterEvent) {
        if (!event.player.isSneaking || event.bed.blockData !is Bed) {
            return
        }

        event.player.bedSpawnLocation = event.bed.location
        openHomeSelectionMenu(
            event.player, event.bed.location.world!!, Position(event.player.bedSpawnLocation!!),
            event.bed.blockData as Bed)
        event.isCancelled = true
    }

    private fun openHomeSelectionMenu(player: Player, world: World, position: Position, bed: Bed) {
        val playerState = players.getByPlayer(player) ?: return

        // Create homes menu
        val gui = ChestGui(1, "Homes")
        val pane = StaticPane(0, 0, 9, 1)
        gui.addPane(pane)
        var lastPaneEntry = 1

        // Add bed player is currently sleeping in
        val currentBedItem = ItemStack(bed.material)
            .name("Current Home")
            .lore("You will respawn at this bed (${position.x} / ${position.y} / ${position.z})")
        val guiCurrentBedItem = GuiItem(currentBedItem) { guiEvent -> guiEvent.isCancelled = true }
        pane.addItem(guiCurrentBedItem, 0, 0)

        val separator = ItemStack(Material.BLACK_STAINED_GLASS_PANE).name(" ")
        val guisSeparator = GuiItem(separator) { guiEvent -> guiEvent.isCancelled = true }
        pane.addItem(guisSeparator, 1, 0)


        // Add existing homes to menu
        val playerHomes = homes.getByPlayer(playerState)
        if (playerHomes.isNotEmpty()) {
            for (i in 0 until playerHomes.count()) {
                val home = playerHomes[i]
                val bedItem = ItemStack(home.colour.toBed())
                    .name(playerHomes[i].name)
                    .lore("Teleports to bed at ${home.position.x}, ${home.position.y}, ${home.position.z}")
                val guiBedItem = GuiItem(bedItem) { player.teleport(home.position.toLocation(home.world)) }
                pane.addItem(guiBedItem, i + 2, 0)
                lastPaneEntry = i + 2
            }
        }

        // Sets new home item based on home state
        val guiItem = if (isHomeAlreadySet(player, position)) {
            val newBedItem = ItemStack(Material.MAGMA_CREAM)
                .name("Home already set")
                .lore("You cannot set an already saved home.")
            GuiItem(newBedItem) { guiEvent -> guiEvent.isCancelled = true }
        }
        else {
            val newBedItem = ItemStack(Material.NETHER_STAR)
                .name("Add new home")
                .lore("Sets your current bed as a saved home.")
            GuiItem(newBedItem) { openHomeCreationMenu(player, world, position, bed) }
        }

        // Add new home item to menu
        pane.addItem(guiItem, lastPaneEntry + 1, 0)
        gui.show(player)
    }

    private fun openHomeCreationMenu(player: Player, world: World, position: Position, bed: Bed) {
        // Create homes menu
        val gui = AnvilGui("Name your home")

        // Add bed menu item
        val firstPane = StaticPane(0, 0, 1, 1)
        val bedItem = ItemStack(bed.material).lore("${position.x}, ${position.y}, ${position.z}")
        val guiItem = GuiItem(bedItem) { guiEvent -> guiEvent.isCancelled = true }
        firstPane.addItem(guiItem, 0, 0)
        gui.firstItemComponent.addPane(firstPane)

        // Add confirm menu item.
        val secondPane = StaticPane(0, 0, 1, 1)
        val confirmItem = ItemStack(Material.NETHER_STAR).name("Confirm")
        val confirmGuiItem = GuiItem(confirmItem) {
            homes.add(Home(Bukkit.getOfflinePlayer(player.uniqueId), gui.renameText, bed.getColour(), world, position))
            openHomeSelectionMenu(player, world, position, bed)
        }
        secondPane.addItem(confirmGuiItem, 0, 0)
        gui.resultComponent.addPane(secondPane)
        gui.show(player)
    }

    private fun isHomeAlreadySet(player: Player, position: Position): Boolean {
        val playerHomes = homes.getByPlayer(players.getByPlayer(player)!!)
        for (home in playerHomes) {
            if (position == home.position) {
                return true
            }
        }
        return false
    }
}