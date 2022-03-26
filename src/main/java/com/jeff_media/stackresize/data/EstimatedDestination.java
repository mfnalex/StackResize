package com.jeff_media.stackresize.data;

import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class EstimatedDestination {
    private final ItemStack destinationItem;
    private final Inventory destinationInventory;
    private final int destinationSlot;

    public static EstimatedDestination getEstimatedDestination(ItemStack item, Inventory destinationInventory) {
        for(int i = 0; i < destinationInventory.getSize(); i++) {
            if(wouldFit(item, destinationInventory, i)) {
                return new EstimatedDestination(item, destinationInventory, i);
            }
        }
        return null;
    }

    private static boolean wouldFit(ItemStack toMove, Inventory destination, int slot) {
        //List<Integer> specialSlots = SpecialInventory.getSpecialSlots(destination.getType());
        List<Integer> readOnlySlots = SpecialInventory.getReadonlySlots(destination.getType());
        ItemStack alreadyThere = destination.getItem(slot);
        if(readOnlySlots.contains(slot)) return false;
        if(alreadyThere == null || alreadyThere.getAmount() == 0) return true;
        if(toMove.isSimilar(alreadyThere) && alreadyThere.getAmount() < alreadyThere.getMaxStackSize()) {
            return true;
        }
        return false;
    }
}
