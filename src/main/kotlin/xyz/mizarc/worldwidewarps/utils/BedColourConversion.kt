package xyz.mizarc.worldwidewarps.utils

import org.bukkit.DyeColor
import org.bukkit.Material

class BedColourConversion {
    companion object {
        private val beds = mapOf(
            DyeColor.BLACK to Material.BLACK_BED,
            DyeColor.BLUE to Material.BLUE_BED,
            DyeColor.CYAN to Material.CYAN_BED,
            DyeColor.GRAY to Material.GRAY_BED,
            DyeColor.GREEN to Material.GREEN_BED,
            DyeColor.LIGHT_BLUE to Material.LIGHT_BLUE_BED,
            DyeColor.LIGHT_GRAY to Material.LIGHT_GRAY_BED,
            DyeColor.LIME to Material.LIME_BED,
            DyeColor.MAGENTA to Material.MAGENTA_BED,
            DyeColor.ORANGE to Material.ORANGE_BED,
            DyeColor.PINK to Material.PINK_BED,
            DyeColor.PURPLE to Material.PURPLE_BED,
            DyeColor.RED to Material.RED_BED,
            DyeColor.WHITE to Material.WHITE_BED,
            DyeColor.YELLOW to Material.YELLOW_BED)

        fun getBedMaterial(dyeColor: DyeColor): Material {
            return if (beds[dyeColor] != null) beds[dyeColor]!! else Material.WHITE_BED
        }
    }
}