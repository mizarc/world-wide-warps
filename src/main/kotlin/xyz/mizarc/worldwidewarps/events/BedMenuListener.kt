package xyz.mizarc.worldwidewarps.events

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.geco.gsit.api.GSitAPI
import dev.geco.gsit.api.event.EntitySitEvent
import dev.geco.gsit.api.event.PlayerGetUpPoseEvent
import dev.geco.gsit.api.event.PlayerPlayerSitEvent
import dev.geco.gsit.api.event.PrePlayerGetUpPoseEvent
import dev.geco.gsit.objects.GetUpReason
import dev.geco.gsit.objects.IGPoseSeat
import org.apache.commons.lang.WordUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.type.Bed
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.inventory.ItemStack
import xyz.mizarc.worldwidewarps.*
import xyz.mizarc.worldwidewarps.utils.lore
import xyz.mizarc.worldwidewarps.utils.name
import xyz.mizarc.worldwidewarps.utils.toBed

class BedMenuListener(private val homes: HomeContainer, private val players: PlayerContainer): Listener {
    private val playersInMenu: MutableList<Player> = mutableListOf()

    @EventHandler
    fun onPlayerGetUpPoseEvent(event: PrePlayerGetUpPoseEvent) {
        if (event.player in playersInMenu) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBedMenuCloseEvent(event: InventoryCloseEvent) {
        if (event.player in playersInMenu) {
            playersInMenu.remove(event.player)
        }
    }

    @EventHandler
    fun onBedShiftClick(event: PlayerBedEnterEvent) {
        if (!event.player.hasPermission("worldwidewarps.action.multihome")
            || !event.player.isSneaking || event.bed.blockData !is Bed) {
            return
        }

        val bed = event.bed.blockData as Bed
        val direction = Direction.fromVector(bed.facing.direction)

        event.isCancelled = true

        val homeBuilder = Home.Builder(event.player, event.bed.world, Position(event.bed.location), bed)
        openHomeSelectionMenu(homeBuilder)
        val pose = GSitAPI.createPose(event.bed, event.player, Pose.SLEEPING, 0.0,
            0.0, 0.0, Direction.toYaw(direction), true)
        event.player.bedSpawnLocation = event.bed.location
    }

    private fun openHomeSelectionMenu(homeBuilder: Home.Builder) {
        val playerState = players.getByPlayer(homeBuilder.player) ?: return

        // Create homes menu
        val gui = ChestGui(1, "Homes")
        val pane = StaticPane(0, 0, 9, 1)
        gui.addPane(pane)
        var lastPaneEntry = 1

        // Add bed player is currently sleeping in
        val currentBedItem = ItemStack(homeBuilder.bed.material)
            .name("Current Home")
            .lore("You will respawn at this bed (${homeBuilder.position.x} / ${homeBuilder.position.y} / ${homeBuilder.position.z})")
        val guiCurrentBedItem = GuiItem(currentBedItem) { guiEvent -> guiEvent.isCancelled = true }
        pane.addItem(guiCurrentBedItem, 0, 0)

        // Add separator
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
                    .lore("Teleports to bed at ${home.position.x}, ${home.position.y}, ${home.position.z}.")
                    .lore("Right click to edit.")
                val guiBedItem = GuiItem(bedItem) { guiEvent ->
                    homeBuilder.sleep = true
                    when (guiEvent.click) {
                        ClickType.RIGHT -> {
                            openHomeEditMenu(homeBuilder, home) }
                        else ->  {
                            GSitAPI.removePose(homeBuilder.player, GetUpReason.GET_UP)
                            teleportToBed(homeBuilder.player, home)
                        }
                    }
                }
                pane.addItem(guiBedItem, i + 2, 0)
                lastPaneEntry = i + 2
            }
        }

        // Sets new home item based on home state
        if (playerHomes.count() < playerState.getHomeLimit())
        {
            val guiItem = if (isHomeAlreadySet(homeBuilder.player, homeBuilder.position)) {
                val newBedItem = ItemStack(Material.MAGMA_CREAM)
                    .name("Home already set")
                    .lore("You cannot set an already saved home.")
                GuiItem(newBedItem) { guiEvent -> guiEvent.isCancelled = true }
            }
            else {
                val newBedItem = ItemStack(Material.NETHER_STAR)
                    .name("Add new home")
                    .lore("Sets your current bed as a saved home.")
                GuiItem(newBedItem) {
                    homeBuilder.sleep = true
                    openHomeCreationMenu(homeBuilder)
                }
            }
            pane.addItem(guiItem, lastPaneEntry + 1, 0)
        }

        gui.setOnClose {
            if (!homeBuilder.sleep) {
                GSitAPI.removePose(homeBuilder.player, GetUpReason.GET_UP)
            }
            homeBuilder.sleep(false)
        }
        gui.show(homeBuilder.player)
        playersInMenu.add(homeBuilder.player)
    }

    private fun openHomeCreationMenu(homeBuilder: Home.Builder) {
        // Create homes menu
        val gui = AnvilGui("Name your home")

        // Add bed menu item
        val firstPane = StaticPane(0, 0, 1, 1)
        val bedItem = ItemStack(homeBuilder.bed.material)
            .lore("${homeBuilder.position.x}, ${homeBuilder.position.y}, ${homeBuilder.position.z}")
        val guiItem = GuiItem(bedItem) { guiEvent -> guiEvent.isCancelled = true }
        firstPane.addItem(guiItem, 0, 0)
        gui.firstItemComponent.addPane(firstPane)

        // Add confirm menu item.
        val secondPane = StaticPane(0, 0, 1, 1)
        val confirmItem = ItemStack(Material.NETHER_STAR).name("Confirm")
        val confirmGuiItem = GuiItem(confirmItem) { guiEvent ->
            homeBuilder.sleep(true)
            guiEvent.isCancelled = true
            homes.add(homeBuilder.name(gui.renameText).build())
            openHomeSelectionMenu(homeBuilder)
        }
        secondPane.addItem(confirmGuiItem, 0, 0)
        gui.resultComponent.addPane(secondPane)
        gui.setOnClose {
            if (!homeBuilder.sleep) {
                GSitAPI.removePose(homeBuilder.player, GetUpReason.GET_UP)
            }
            homeBuilder.sleep(false)
        }
        gui.show(homeBuilder.player)
    }

    private fun openHomeEditMenu(homeBuilder: Home.Builder, editingHome: Home) {
        // Create edit menu
        val name = editingHome.name.ifEmpty {
            WordUtils.capitalizeFully(editingHome.colour.toBed().name.replace("_", " "))
        }
        val gui = ChestGui(1, "Editing $name")
        val pane = StaticPane(0, 0, 9, 1)
        gui.addPane(pane)

        // Add Edit button
        val renameItem = ItemStack(Material.NAME_TAG)
            .name("Rename Home")
        val guiRenameItem = GuiItem(renameItem) {
            homeBuilder.sleep(true)
            openHomeRenameMenu(homeBuilder, editingHome) }
        pane.addItem(guiRenameItem, 2, 0)

        // Add Remove button
        val removeItem = ItemStack(Material.REDSTONE)
            .name("Delete Home")
        val guiRemoveItem = GuiItem(removeItem) {
            homeBuilder.sleep(true)
            homes.remove(editingHome)
            openHomeSelectionMenu(homeBuilder)
        }
        pane.addItem(guiRemoveItem, 4, 0)

        // Add Go Back button
        val goBackItem = ItemStack(Material.NETHER_STAR)
            .name("Go Back")
        val guiGoBackItem = GuiItem(goBackItem) {
            homeBuilder.sleep(true)
            openHomeSelectionMenu(homeBuilder) }
        pane.addItem(guiGoBackItem, 6, 0)
        gui.setOnClose {
            if (!homeBuilder.sleep) {
                GSitAPI.removePose(homeBuilder.player, GetUpReason.GET_UP)
            }
            homeBuilder.sleep(false)
        }
        gui.show(homeBuilder.player)
    }

    private fun openHomeRenameMenu(homeBuilder: Home.Builder, editingHome: Home) {
        // Create homes menu
        val gui = AnvilGui("Renaming ${editingHome.name}")

        // Add bed menu item
        val firstPane = StaticPane(0, 0, 1, 1)
        val bedItem = ItemStack(
            editingHome.colour.toBed().createBlockData().material)
            .lore("${editingHome.position.x}, ${editingHome.position.y}, ${editingHome.position.z}")
        val guiItem = GuiItem(bedItem) { guiEvent -> guiEvent.isCancelled = true }
        firstPane.addItem(guiItem, 0, 0)
        gui.firstItemComponent.addPane(firstPane)

        // Add confirm menu item.
        val secondPane = StaticPane(0, 0, 1, 1)
        val confirmItem = ItemStack(Material.NETHER_STAR).name("Confirm")
        val confirmGuiItem = GuiItem(confirmItem) { guiEvent ->
            homeBuilder.sleep(true)
            val newHome = Home(editingHome.id, editingHome.player,
                gui.renameText, editingHome.colour, editingHome.world, editingHome.position, editingHome.direction)
            homes.update(newHome)
            openHomeEditMenu(homeBuilder, editingHome)
            guiEvent.isCancelled = true
        }
        secondPane.addItem(confirmGuiItem, 0, 0)
        gui.resultComponent.addPane(secondPane)
        gui.setOnClose {
            if (!homeBuilder.sleep) {
                GSitAPI.removePose(homeBuilder.player, GetUpReason.GET_UP)
            }
            homeBuilder.sleep(false)
        }
        gui.show(Bukkit.getPlayer(editingHome.player.uniqueId)!!)
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

    private fun teleportToBed(player: Player, home: Home) {
        player.teleport(Location(home.world, home.position.x.toDouble(), home.position.y.toDouble() + 1, home.position.z.toDouble()))
        val sleepingLocation = Location(home.world, home.position.x.toDouble(), home.position.y.toDouble(), home.position.z.toDouble())
        GSitAPI.createPose(sleepingLocation.block, player, Pose.SLEEPING,
            0.0, 0.0, 0.0, Direction.toYaw(home.direction), true)
        player.bedSpawnLocation = home.position.toLocation(home.world)
    }
}