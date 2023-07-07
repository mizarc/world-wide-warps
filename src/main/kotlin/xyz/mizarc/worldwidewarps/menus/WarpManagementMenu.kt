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
        val existingWarp = warpRepository.getByPosition(warpBuilder.position)
        if (existingWarp == null) {
            openWarpCreationMenu()
            return
        }

        openWarpEditMenu(existingWarp)
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

    fun openWarpNamingMenu(existing_name: Boolean = false) {
        // Create homes menu
        val gui = AnvilGui("Naming ${warpBuilder.name}")

        // Add lodestone menu item
        val firstPane = StaticPane(0, 0, 1, 1)
        val lodestoneItem = ItemStack(Material.LODESTONE)
            .name(warpBuilder.name)
            .lore("${warpBuilder.position.x}, ${warpBuilder.position.y}, ${warpBuilder.position.z}")
        val guiItem = GuiItem(lodestoneItem) { guiEvent -> guiEvent.isCancelled = true }
        firstPane.addItem(guiItem, 0, 0)
        gui.firstItemComponent.addPane(firstPane)

        // Add message menu item if name is already taken
        if (existing_name) {
            val secondPane = StaticPane(0, 0, 1, 1)
            val paperItem = ItemStack(Material.PAPER)
                .name("That name has already been taken")
            val guiPaperItem = GuiItem(paperItem) { guiEvent -> guiEvent.isCancelled = true }
            secondPane.addItem(guiPaperItem, 0, 0)
            gui.secondItemComponent.addPane(secondPane)
        }

        // Add confirm menu item.
        val thirdPane = StaticPane(0, 0, 1, 1)
        val confirmItem = ItemStack(Material.NETHER_STAR).name("Confirm")
        val confirmGuiItem = GuiItem(confirmItem) { guiEvent ->
            warpBuilder.name = gui.renameText
            if (warpRepository.getByName(gui.renameText) != null) {
                openWarpNamingMenu(existing_name = true)
                return@GuiItem
            }
            val warp = warpBuilder.build()
            warpRepository.add(warp)
            openWarpEditMenu(warp)
            guiEvent.isCancelled = true
        }
        thirdPane.addItem(confirmGuiItem, 0, 0)
        gui.resultComponent.addPane(thirdPane)
        gui.show(Bukkit.getPlayer(warpBuilder.player.uniqueId)!!)
    }

    fun openWarpEditMenu(warp: Warp) {
        val gui = ChestGui(1, "Warp '${warp.name}'")
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
        val guiRenamingItem = GuiItem(renamingItem) { openWarpRenamingMenu(warp) }
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

    fun openWarpRenamingMenu(warp: Warp, existing_name: Boolean = false) {
        // Create homes menu
        val gui: AnvilGui = AnvilGui("Renaming Warp")

        // Add lodestone menu item
        val firstPane = StaticPane(0, 0, 1, 1)
        val lodestoneItem = ItemStack(Material.LODESTONE)
            .name(warp.name)
            .lore("${warpBuilder.position.x}, ${warpBuilder.position.y}, ${warpBuilder.position.z}")
        val guiItem = GuiItem(lodestoneItem) { guiEvent -> guiEvent.isCancelled = true }
        firstPane.addItem(guiItem, 0, 0)
        gui.firstItemComponent.addPane(firstPane)

        // Add message menu item if name is already taken
        if (existing_name) {
            val secondPane = StaticPane(0, 0, 1, 1)
            val paperItem = ItemStack(Material.PAPER)
                .name("That name has already been taken")
            val guiPaperItem = GuiItem(paperItem) { guiEvent -> guiEvent.isCancelled = true }
            secondPane.addItem(guiPaperItem, 0, 0)
            gui.secondItemComponent.addPane(secondPane)
        }

        // Add confirm menu item.
        val thirdPane = StaticPane(0, 0, 1, 1)
        val confirmItem = ItemStack(Material.NETHER_STAR).name("Confirm")
        val confirmGuiItem = GuiItem(confirmItem) { guiEvent ->
            val newWarp = warp.copy()
            warp.name = gui.renameText
            if (warpRepository.getByName(newWarp.name) != null) {
                openWarpRenamingMenu(warp, existing_name = true)
                return@GuiItem
            }
            warpRepository.update(warp)
            openWarpEditMenu(warp)
            guiEvent.isCancelled = true
        }
        thirdPane.addItem(confirmGuiItem, 0, 0)
        gui.resultComponent.addPane(thirdPane)
        gui.show(Bukkit.getPlayer(warpBuilder.player.uniqueId)!!)
    }
}