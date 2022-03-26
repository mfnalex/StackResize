package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.StackResize;
import com.jeff_media.stackresize.data.SpecialInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GenericStackListener implements Listener {

    private static final StackResize main = StackResize.getInstance();

    private static boolean isFirstSlotSameMaterial(Inventory inventory, ItemStack item) {
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack toCheck = inventory.getItem(i);
            if(toCheck == null || toCheck.getAmount() == 0) return false;
            if(toCheck.isSimilar(item)) return true;
        }
        return false;
    }

    @EventHandler
    public void onShiftStack(InventoryClickEvent event) {
        if(event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
        if(event.getClickedInventory() == null) return;
        Inventory destination = event.getView().getTopInventory();
        if(event.getClickedInventory().equals(event.getView().getTopInventory())) destination = event.getView().getBottomInventory();
        ItemStack currentItem = event.getCurrentItem();
        if(currentItem == null || currentItem.getAmount() == 0) return;
        if(!main.isChanged(currentItem.getType())) return;
        if(!isFirstSlotSameMaterial(destination,currentItem));
        //event.setCancelled(true);
    }

    @EventHandler
    public void onStackDrag(InventoryDragEvent event) {

    }

    @EventHandler
    public void onStack(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        if(action != InventoryAction.PLACE_ALL
                && action != InventoryAction.PLACE_ONE
                && action != InventoryAction.PLACE_SOME) {
            return;
        }
        int slot = event.getSlot();
        Inventory clickedInv = event.getClickedInventory();
        if(clickedInv == null) {
            return;
        }
        if(!clickedInv.equals(event.getView().getTopInventory())) {
            return;
        }
        if(!SpecialInventory.isSpecialSlot(clickedInv.getType(),slot)) {
            return;
        }
        List<Integer> specialSlots = SpecialInventory.getSpecialSlots(clickedInv.getType());
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if(SpecialInventory.isSpecialSlot(clickedInv.getType(),event.getSlot())) {
            if(cursorItem != null && cursorItem.getAmount()>1 && main.isChanged(cursorItem.getType())) {
                ItemStack existing = clickedInv.getItem(event.getSlot());
                if(existing == null || existing.getAmount() == 0 || existing.getType() == cursorItem.getType()) {
                    if(action != InventoryAction.PLACE_ONE && (existing == null || existing.getAmount()==0)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        for(int specialSlot : specialSlots) {
            if(clickedInv.getItem(specialSlot) == null) {
                continue;
            }
            if(currentItem != null && clickedInv.getItem(specialSlot).getType() == currentItem.getType()
                || currentItem != null && clickedInv.getItem(specialSlot).getType() == currentItem.getType()) {
                event.setCancelled(true);
            }
        }
    }
}
