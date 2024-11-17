package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.StackResize;
import com.jeff_media.stackresize.data.SpecialInventory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BrewingListener implements Listener {

    private static final StackResize main = StackResize.getInstance();

    public int getEmptySlot(Inventory brewing) {
        List<Integer> slots = SpecialInventory.getSpecialSlots(InventoryType.BREWING);

        for (int slot : slots) {
            if (brewing.getItem(slot) == null) return slot;
        }

        return -999;
    }

    private void moveOnlyOne(Inventory brewingInv, ItemStack currentItem) {
        int destSlot = getEmptySlot(brewingInv);
        if (destSlot >= 0) {
            ItemStack clone = currentItem.clone();
            clone.setAmount(1);
            currentItem.setAmount(currentItem.getAmount() - 1);
            brewingInv.setItem(destSlot, clone);
        }
    }

    private boolean isPotion(ItemStack item) {
        return
                item.getType() == Material.POTION ||
                        item.getType() == Material.SPLASH_POTION ||
                        item.getType() == Material.LINGERING_POTION;
    }

    @EventHandler
    public void onShiftClickFromPlayer(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;

        Inventory origin = event.getClickedInventory();
        Inventory destination = event.getView().getTopInventory();
        if (origin.getType() != InventoryType.PLAYER || destination.getType() != InventoryType.BREWING) return;

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getAmount() == 0) return;
        if (!isPotion(currentItem)) return;
        if (!main.isChanged(currentItem.getType())) return;

        moveOnlyOne(destination, currentItem);
        event.setCancelled(true);
    }

}
