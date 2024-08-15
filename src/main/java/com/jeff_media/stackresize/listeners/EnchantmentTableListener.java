package com.jeff_media.stackresize.listeners;

import com.jeff_media.jefflib.MaterialUtils;
import com.jeff_media.jefflib.Tasks;
import com.jeff_media.stackresize.BugHandler;
import com.jeff_media.stackresize.StackResize;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class EnchantmentTableListener implements Listener {

    private static final StackResize main = StackResize.getInstance();

    private final Set<Material> stackSizes = new HashSet<>();

    @EventHandler
    public void onInsertToEnchantmentTable(PrepareItemEnchantEvent event) {
        if (!main.isChangedDangerously(event.getItem())) return;
        Material material = event.getItem().getType();
        if (stackSizes.contains(material)) return;
        stackSizes.add(material);
        int stackSize = material.getMaxStackSize();
        MaterialUtils.setMaxStackSize(material, 1);
        Tasks.nextTick(() -> {
            MaterialUtils.setMaxStackSize(material, stackSize);
            stackSizes.remove(material);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchantItemWithStackedEnchantedBook(InventoryClickEvent event) {
        if (main.getConfig().getBoolean("ignore-cancelled-click-on-enchanted-items")) {
            if (event.isCancelled()) return;
        }
        //System.out.println("EnchantItemEvent");
        if (!(event.getInventory() instanceof AnvilInventory)) {
            //System.out.println("Not an AnvilInventory but " + event.getInventory().getClass().getName());
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            //System.out.println("Not a result slot");
            return;
        }
        AnvilInventory inventory = (AnvilInventory) event.getInventory();
        if (!main.isChangedDangerously(inventory.getItem(1))) {
            //System.out.println("Secondary item is not changed");
            return;
        }
        ItemStack second = inventory.getItem(1);
        if (second == null) {
            //System.out.println("Second item is null");
            return;
        }
        if (second.getType() != Material.ENCHANTED_BOOK) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        BugHandler.fixDisappearing((Player) event.getWhoClicked(), () -> inventory.getItem(1), item -> inventory.setItem(1, item), true);
    }

}
