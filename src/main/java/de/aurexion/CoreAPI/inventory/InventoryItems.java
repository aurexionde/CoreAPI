package de.aurexion.CoreAPI.inventory;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public enum InventoryItems {
    SINGLE_FILLER(
            new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .setName(" ")
                    .setCustomModelData(3000)
                    .build()
    ),

    DOUBLE_FILLER(
            new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .setName(" ")
                    .setCustomModelData(3001)
                    .build()
    ),

    FILLER_CANCEL(
            new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .setName("§cAbbrechen")
                    .setCustomModelData(3002)
                    .build()
    );

    private final ItemStack item;

    InventoryItems(ItemStack item) {
        this.item = item;
    }
}
