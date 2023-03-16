package com.jeff_media.stackresize.data;

import com.jeff_media.jefflib.NumberUtils;
import lombok.Data;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class EstimatedDestination {
    private final ItemStack destinationItem;
    private final Inventory destinationInventory;
    private final int destinationSlot;

//    public static EstimatedDestination getEstimatedDestination(ItemStack item, Inventory destinationInventory) {
//        for(int i = 0; i < destinationInventory.getSize(); i++) {
//            if(wouldFit(item, destinationInventory, i)) {
//                return new EstimatedDestination(item, destinationInventory, i);
//            }
//        }
//        return null;
//    }
//
//    private static boolean wouldFit(ItemStack toMove, Inventory destination, int slot) {
//        System.out.println("Would fit?????????????????????????????????????");
//        //List<Integer> specialSlots = SpecialInventory.getSpecialSlots(destination.getType());
//        List<Integer> readOnlySlots = SpecialInventory.getReadonlySlots(destination.getType());
//        System.out.println("Destination type: " + destination.getType());
//        System.out.println("Slot: " + slot);
//        if(destination.getType() == InventoryType.BREWING && slot >= 0 && slot <= 2) {
//            System.out.println("Destination is brewing");
//            return wouldFit_Brewing(toMove, destination, slot);
//        }
//        ItemStack alreadyThere = destination.getItem(slot);
//        if(readOnlySlots.contains(slot)) return false;
//        if(alreadyThere == null || alreadyThere.getAmount() == 0) return true;
//        if(toMove.isSimilar(alreadyThere) && alreadyThere.getAmount() < alreadyThere.getMaxStackSize()) {
//            return true;
//        }
//        return false;
//    }
//
//    private static boolean wouldFit_Brewing(ItemStack toMove, Inventory destination, int slot) {
//        ItemStack alreadyThere = destination.getItem(slot);
//        if(alreadyThere == null || alreadyThere.getAmount() == 0) {
//            System.out.println("Already there is null or 0");
//            return true;
//        }
//        if(toMove.isSimilar(alreadyThere) && alreadyThere.getAmount() < alreadyThere.getMaxStackSize()) {
//            System.out.println("Already there is similar and has space");
//            return true;
//        }
//        return false;
//    }
}
