package de.aurexion.CoreAPI.inventory;

import de.aurexion.CoreAPI.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Simple inventory builder for creating custom GUIs
 */
public class InventoryBuilder {
    private static InventoryBuilderListener listener;
    private static final Map<UUID, BuilderState> openMap = new HashMap<>();

    private final String title;
    private final int rows;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Map<Integer, BiConsumer<Player, InventoryClickEvent>> itemHandlers = new HashMap<>();

    private ItemStack filler = null;
    private boolean itemsRemovable = false;
    private BiConsumer<Player, InventoryClickEvent> clickHandler = (p, e) -> {};
    private Inventory builtInventory;

    private static class BuilderState {
        final InventoryBuilder builder;

        BuilderState(InventoryBuilder b) {
            this.builder = b;
        }
    }

    private static class BuilderHolder implements InventoryHolder {
        final InventoryBuilder builder;
        Inventory inv;

        BuilderHolder(InventoryBuilder b) {
            this.builder = b;
        }

        void setInventory(Inventory inv) {
            this.inv = inv;
        }

        @Override
        public Inventory getInventory() {
            return inv;
        }
    }

    /**
     * Create a new InventoryBuilder
     * @param title inventory title
     * @param rows number of rows (1-6)
     */
    public InventoryBuilder(String title, int rows) {
        this.title = title == null ? "" : title;
        if (rows < 1) rows = 1;
        if (rows > 6) rows = 6;
        this.rows = rows;
    }

    /**
     * Create a default 3-row InventoryBuilder
     */
    public InventoryBuilder() {
        this("", 3);
    }

    /**
     * Set an item at a specific slot
     */
    public InventoryBuilder setItem(int slot, ItemStack item) {
        if (slot < 0 || slot >= rows * 9) return this;
        if (item == null) {
            items.remove(slot);
            itemHandlers.remove(slot);
        } else {
            items.put(slot, item);
        }
        return this;
    }

    /**
     * Set an item with a click handler
     */
    public InventoryBuilder setItem(int slot, ItemStack item, BiConsumer<Player, InventoryClickEvent> handler) {
        if (slot < 0 || slot >= rows * 9) return this;
        if (item == null) {
            items.remove(slot);
            itemHandlers.remove(slot);
        } else {
            items.put(slot, item);
            if (handler != null) itemHandlers.put(slot, handler);
            else itemHandlers.remove(slot);
        }
        return this;
    }

    /**
     * Set an item with a simple click handler
     */
    public InventoryBuilder setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        if (handler != null) {
            return setItem(slot, item, (p, e) -> handler.accept(e));
        } else {
            return setItem(slot, item, (BiConsumer<Player, InventoryClickEvent>) null);
        }
    }

    /**
     * Fill a range of slots
     */
    public InventoryBuilder fillRange(int from, int to, ItemStack item) {
        int max = rows * 9 - 1;
        int f = Math.max(0, from);
        int t = Math.min(max, to);
        for (int i = f; i <= t; i++) setItem(i, item);
        return this;
    }

    /**
     * Set filler item for empty slots
     */
    public InventoryBuilder setFiller(ItemStack filler) {
        this.filler = filler;
        return this;
    }

    /**
     * Set whether items can be removed
     */
    public InventoryBuilder setItemsRemovable(boolean removable) {
        this.itemsRemovable = removable;
        return this;
    }

    /**
     * Set global click handler
     * @deprecated Use per-item handlers instead
     */
    @Deprecated
    public InventoryBuilder setClickHandler(BiConsumer<Player, InventoryClickEvent> handler) {
        if (handler != null) this.clickHandler = handler;
        return this;
    }

    /**
     * Set global click handler (simple)
     * @deprecated Use per-item handlers instead
     */
    @Deprecated
    public InventoryBuilder setClickHandler(Consumer<InventoryClickEvent> handler) {
        if (handler != null) {
            this.clickHandler = (p, e) -> handler.accept(e);
        }
        return this;
    }

    private int findNextFreeSlot() {
        int size = rows * 9;
        for (int i = 0; i < size; i++) {
            if (!items.containsKey(i)) return i;
        }
        return -1;
    }

    /**
     * Add item to next free slot
     */
    public InventoryBuilder addItem(ItemStack item) {
        if (item == null) return this;
        int slot = findNextFreeSlot();
        if (slot < 0) return this;
        return setItem(slot, item);
    }

    /**
     * Add item with handler to next free slot
     */
    public InventoryBuilder addItem(ItemStack item, BiConsumer<Player, InventoryClickEvent> handler) {
        if (item == null) return this;
        int slot = findNextFreeSlot();
        if (slot < 0) return this;
        return setItem(slot, item, handler);
    }

    /**
     * Add item with simple handler to next free slot
     */
    public InventoryBuilder addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        if (item == null) return this;
        int slot = findNextFreeSlot();
        if (slot < 0) return this;
        return setItem(slot, item, handler);
    }

    /**
     * Build the inventory
     */
    public Inventory build() {
        ensureListenerRegistered();

        if (builtInventory != null) return builtInventory;

        int size = rows * 9;
        BuilderHolder holder = new BuilderHolder(this);
        Inventory inv = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inv);

        for (int i = 0; i < size; i++) {
            ItemStack it = items.get(i);
            if (it != null) inv.setItem(i, it);
            else if (filler != null) inv.setItem(i, filler.clone());
        }

        this.builtInventory = inv;
        return inv;
    }

    /**
     * Open the GUI for a player
     */
    public void open(Player player) {
        Inventory inv = build();
        openMap.put(player.getUniqueId(), new BuilderState(this));
        player.openInventory(inv);
    }

    /**
     * Refresh the GUI for all viewers
     */
    public void refresh() {
        this.builtInventory = null;
        Inventory inv = build();

        List<UUID> keys = new ArrayList<>(openMap.keySet());
        for (UUID u : keys) {
            BuilderState s = openMap.get(u);
            if (s == null) continue;
            if (s.builder != this) continue;

            Player player = Bukkit.getPlayer(u);
            if (player == null || !player.isOnline()) {
                openMap.remove(u);
                continue;
            }

            player.openInventory(inv);
        }
    }

    // Lazy listener registration
    private static void ensureListenerRegistered() {
        if (listener == null) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("AurexionCore");
            if (plugin == null) {
                throw new IllegalStateException("AurexionCore plugin not found!");
            }
            listener = new InventoryBuilderListener();
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    /**
     * Internal listener for inventory events
     */
    private static class InventoryBuilderListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory top = event.getView().getTopInventory();
            InventoryHolder holder = top.getHolder();
            if (!(holder instanceof BuilderHolder)) return;

            BuilderHolder bh = (BuilderHolder) holder;
            InventoryBuilder builder = bh.builder;
            if (builder == null) return;

            HumanEntity clicker = event.getWhoClicked();
            if (!(clicker instanceof Player)) return;
            Player player = (Player) clicker;

            int raw = event.getRawSlot();
            if (raw < 0 || raw >= builder.rows * 9) return;

            ItemStack clicked = top.getItem(raw);
            ItemStack filler = builder.filler;

            if (filler != null && areSimilar(clicked, filler)) {
                event.setCancelled(true);
                return;
            }

            BiConsumer<Player, InventoryClickEvent> slotHandler = builder.itemHandlers.get(raw);
            if (slotHandler != null) {
                slotHandler.accept(player, event);
            } else {
                builder.clickHandler.accept(player, event);
            }

            if (!builder.itemsRemovable) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            Inventory top = event.getView().getTopInventory();
            InventoryHolder holder = top.getHolder();
            if (!(holder instanceof BuilderHolder)) return;

            BuilderHolder bh = (BuilderHolder) holder;
            InventoryBuilder builder = bh.builder;
            if (builder == null) return;

            openMap.remove(event.getPlayer().getUniqueId());
        }

        private boolean areSimilar(ItemStack a, ItemStack b) {
            if (a == null || b == null) return false;
            if (a.getType() != b.getType()) return false;
            if (a.getItemMeta() == null && b.getItemMeta() == null) return true;
            if (a.getItemMeta() == null || b.getItemMeta() == null) return false;

            String da = a.getItemMeta().hasDisplayName() ? a.getItemMeta().getDisplayName() : null;
            String db = b.getItemMeta().hasDisplayName() ? b.getItemMeta().getDisplayName() : null;
            if (!Objects.equals(da, db)) return false;

            Integer am = a.getItemMeta().hasCustomModelData() ? a.getItemMeta().getCustomModelData() : null;
            Integer bm = b.getItemMeta().hasCustomModelData() ? b.getItemMeta().getCustomModelData() : null;
            return Objects.equals(am, bm);
        }
    }

    /**
     * Unregister listener (called on plugin disable)
     */
    public static void unregisterAll() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
            listener = null;
            openMap.clear();
        }
    }
}
