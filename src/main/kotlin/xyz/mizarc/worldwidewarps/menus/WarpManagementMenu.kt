package xyz.mizarc.worldwidewarps.menus

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.mizarc.worldwidewarps.Warp
import xyz.mizarc.worldwidewarps.WarpRepository
import xyz.mizarc.worldwidewarps.utils.lore
import xyz.mizarc.worldwidewarps.utils.name

class WarpManagementMenu(private val warpRepository: WarpRepository, private val warpBuilder: Warp.Builder) {

    fun openWarpManagementMenu() {
        if (warpRepository.getByPosition(warpBuilder.position).isEmpty()) {
            openWarpCreationMenu()
            return
        }
        openWarpEditMenu()
    }

    fun openWarpCreationMenu() {
        val gui = ChestGui(1, "Warp Creation")
        val pane = StaticPane(0, 0, 9, 1)
        gui.addPane(pane)

        // Add warp creation icon
        val iconEditorItem = ItemStack(Material.LODESTONE)
            .name("Create Warp")
            .lore("Creating a warp establishes this lodestone as a public teleportation point")
        val guiIconEditorItem = GuiItem(iconEditorItem) { openWarpNamingMenu() }
        pane.addItem(guiIconEditorItem, 4, 0)
        gui.show(Bukkit.getPlayer(warpBuilder.player.uniqueId)!!)
    }

    fun openWarpNamingMenu() {
        // Create homes menu
        val gui = AnvilGui("Naming ${warpBuilder.name}")

        // Add lodestone menu item
        val firstPane = StaticPane(0, 0, 1, 1)
        val lodestoneItem = ItemStack(Material.LODESTONE)
            .lore("${warpBuilder.position.x}, ${warpBuilder.position.y}, ${warpBuilder.position.z}")
        val guiItem = GuiItem(lodestoneItem) { guiEvent -> guiEvent.isCancelled = true }
        firstPane.addItem(guiItem, 0, 0)
        gui.firstItemComponent.addPane(firstPane)

        // Add confirm menu item.
        val secondPane = StaticPane(0, 0, 1, 1)
        val confirmItem = ItemStack(Material.NETHER_STAR).name("Confirm")
        val confirmGuiItem = GuiItem(confirmItem) { guiEvent ->
            warpBuilder.name = gui.renameText
            warpRepository.add(warpBuilder.build())
            openWarpEditMenu()
            guiEvent.isCancelled = true
        }
        secondPane.addItem(confirmGuiItem, 0, 0)
        gui.resultComponent.addPane(secondPane)
        gui.show(Bukkit.getPlayer(warpBuilder.player.uniqueId)!!)
    }

    fun openWarpEditMenu() {
        val gui = ChestGui(1, "Warp")
        val pane = StaticPane(0, 0, 9, 1)
        gui.addPane(pane)

        // Add icon editor button
        val iconEditorItem = ItemStack(Material.LODESTONE)
            .name("Edit Warp Icon")
            .lore("Changes the icon that shows up on the warp list")
        val guiIconEditorItem = GuiItem(iconEditorItem) { guiEvent -> guiEvent.isCancelled = true }
        pane.addItem(guiIconEditorItem, 0, 0)

        // Add renaming icon
        val renamingItem = ItemStack(Material.NAME_TAG)
            .name("Rename Warp")
            .lore("Renames this warp")
        val guiRenamingItem = GuiItem(renamingItem) { guiEvent -> guiEvent.isCancelled = true }
        pane.addItem(guiRenamingItem, 2, 0)

        // Add direction icon
        val directionItem = ItemStack(Material.COMPASS)
            .name("Change Facing Direction")
            .lore("Renames this warp")
        val guiDirectionItem = GuiItem(directionItem) { guiEvent -> guiEvent.isCancelled = true }
        pane.addItem(guiDirectionItem, 4, 0)

        // Add player count icon
        val playerCountItem = ItemStack(Material.PLAYER_HEAD)
            .name("Player Count:")
            .lore("")
        val guiPlayerCountItem = GuiItem(playerCountItem) { guiEvent -> guiEvent.isCancelled = true }
        pane.addItem(guiPlayerCountItem, 6, 0)

        // Add warp delete icon
        val deleteItem = ItemStack(Material.REDSTONE)
            .name("Delete Warp")
        val guiDeleteItem = GuiItem(deleteItem) { guiEvent -> guiEvent.isCancelled = true }
        pane.addItem(guiDeleteItem, 8, 0)

        gui.show(warpBuilder.player)
    }
}