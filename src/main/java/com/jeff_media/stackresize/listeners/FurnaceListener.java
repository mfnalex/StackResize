package com.jeff_media.stackresize.listeners;

import de.jeff_media.jefflib.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Only used to avoid a stack of lava bucket being moved
 */
public class FurnaceListener implements Listener {

    private static final List<Material> DANGEROUS_FUELS = Arrays.asList(Material.LAVA_BUCKET);
    private static final int FUEL_SLOT_IN_DRAG_EVENT = 1;

    private static boolean isDangerousFuel(ItemStack item) {
        if(item == null) return false;
        return DANGEROUS_FUELS.contains(item.getType());
    }

    private static boolean isFurnaceInv(Inventory inventory) {
        if(inventory == null) return false;
        return inventory instanceof FurnaceInventory;
    }

    /**
     * Checks whether the clicked slot is the fuel slot. Has to be used with event#getSlot(), NOT event#getRawSlot
     * @param slot Slot, NOT raw slot
     * @return true when it's the fuel slot
     */
    private static boolean isFurnaceSlot(int slot) {
        return slot == 1;
    }

    @EventHandler
    public void onFurnace(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(!(event.getView().getTopInventory() instanceof FurnaceInventory)) return;
        //DebugUtils.Events.debug(event);
        Player player = (Player) event.getWhoClicked();
        FurnaceInventory topInv = (FurnaceInventory) event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();
        Inventory clickedInv = event.getClickedInventory();
        InventoryAction action = event.getAction();
        int slot = event.getSlot();
        ItemStack cursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack alreadyInSlot = clickedInv == null ? null : clickedInv.getItem(slot);
        InventoryType.SlotType slotType = event.getSlotType();
        ItemStack fuel = topInv.getFuel();
        if(clickedInv == null) return;

        switch (action) {
            case PLACE_ONE:
                if(!isFurnaceInv(clickedInv)) return;
                //if(!isFurnaceSlot(slot)) return;
                if(slotType != InventoryType.SlotType.FUEL) return;
                if(!isDangerousFuel(cursor)) return;
                if(alreadyInSlot != null) event.setCancelled(true);
                break;
            case PLACE_ALL:
                if(!isFurnaceInv(clickedInv)) return;
                //if(!isFurnaceSlot(slot)) return;
                if(slotType != InventoryType.SlotType.FUEL) return;
                if(!isDangerousFuel(cursor)) return;
                if(cursor.getAmount() > 1) event.setCancelled(true);
                if(alreadyInSlot != null && alreadyInSlot.getType() == cursor.getType()) event.setCancelled(true);
                addOnlyOne(topInv,cursor,fuel);
                break;
            case MOVE_TO_OTHER_INVENTORY:
                if(!isDangerousFuel(currentItem)) return;
                if(!clickedInv.equals(bottomInv)) return;
                if(currentItem.getAmount()>1) event.setCancelled(true);
                if(fuel != null && fuel.getType() == currentItem.getType()) event.setCancelled(true);
                addOnlyOne(topInv, currentItem, fuel);
        }

        if(event.isCancelled()) {
            player.updateInventory();
        }
    }

    private void addOnlyOne(FurnaceInventory topInv, ItemStack currentItem, ItemStack fuel) {
        if(fuel == null) {
            ItemStack clone = currentItem.clone();
            clone.setAmount(1);
            topInv.setFuel(clone);
            currentItem.setAmount(currentItem.getAmount()-1);
        }
    }

    @EventHandler
    public void onFurnace(InventoryDragEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(event.getType() == DragType.SINGLE) return;
        Inventory inv = event.getInventory();
        if(!(inv instanceof FurnaceInventory)) return;
        FurnaceInventory furnaceInv = (FurnaceInventory) event.getInventory();
        ItemStack cursor = event.getOldCursor();
        ItemStack fuel = furnaceInv.getFuel();
        if(!isDangerousFuel(cursor)) return;
        if(cursor.getAmount()==1) return;
        event.getInventorySlots().forEach(slot -> {
            if(isFurnaceSlot(slot) && isDangerousFuel(fuel)) {
                event.setCancelled(true);
            }
        });
        ItemStack newFuel = event.getNewItems().get(1);
        if(newFuel != null && newFuel.getAmount()>1) event.setCancelled(true);
        /*if(event.getInventorySlots().size()==1 && event.getInventorySlots().contains(FUEL_SLOT_IN_DRAG_EVENT)) {
            addOnlyOne(furnaceInv,event.getOldCursor(),fuel);
        }*/
        if(event.isCancelled()) ((Player)event.getWhoClicked()).updateInventory();
    }


}
