package de.aurexion.CoreAPI.inventory;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemBuilder {
    ItemStack itemStack;
    org.bukkit.inventory.meta.ItemMeta itemMeta;

    public ItemBuilder(@NonNull Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(@NonNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setName(@NonNull String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setName(@NonNull TextComponent component) {
        itemMeta.displayName(component);
        return this;
    }

    public ItemBuilder setLore(@NonNull List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder setLore(@NonNull Set<Component> lore) {
        itemMeta.lore(lore.stream().toList());
        return this;
    }

    public ItemBuilder setCustomModelData(Integer data) {
        itemMeta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        if (glowing) {
            itemMeta.addEnchant(Enchantment.BREACH, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            itemMeta.removeEnchant(Enchantment.BREACH);
        }
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
