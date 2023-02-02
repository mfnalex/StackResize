package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.StackResize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jeff_media.stackresize.listeners.HopperListener.MoveReaction.*;

public class HopperListener implements Listener {

    private static final StackResize main = StackResize.getInstance();
    private static final List<InventoryType> specialInventoryTypes = Arrays.asList(
            InventoryType.BREWING,
            InventoryType.FURNACE,
            InventoryType.BLAST_FURNACE,
            InventoryType.SMOKER
    );
    private static final List<Material> neverStackInFurnace = Collections.singletonList(Material.LAVA_BUCKET);

    private static boolean isSpecialInventory(Inventory inventory) {
        return specialInventoryTypes.contains(inventory.getType());
    }

    enum MoveReaction {
        ALLOW, PREVENT, ALLOW_ONE
    }

    private static MoveReaction getMoveReaction(Inventory destination, ItemStack toMove) {
        switch (destination.getType()) {
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
                if(toMove.getType()!=Material.LAVA_BUCKET) return ALLOW;
                FurnaceInventory furnaceInv = (FurnaceInventory) destination;
                ItemStack fuel = furnaceInv.getFuel();
                if(fuel == null) return ALLOW_ONE;
                return PREVENT;
            case BREWING:
                return isPotion(toMove.getType()) ? ALLOW_ONE : ALLOW;
        }
        // Cannot check Composter InventoryType in 1.16, so lets do it manually
        InventoryHolder holder = destination.getHolder();
        if(holder != null && holder instanceof Hopper) {
            return ALLOW_ONE;
        }
        return ALLOW;
    }

    private static boolean isPotion(Material material) {
        return material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHopperMove(InventoryMoveItemEvent event) {
        ItemStack item = event.getItem();
        if(item.getAmount()==1) return;
        if(!main.isChanged(item.getType())) return;
        Inventory destination = event.getDestination();
        if(!isSpecialInventory(destination)) return;
        MoveReaction reaction = getMoveReaction(destination,event.getItem());
        if(reaction == ALLOW) {
            return;
        } else if(reaction == PREVENT) {
            event.setCancelled(true);
            return;
        }
        ItemStack itemToMove = event.getItem().clone();
        ItemStack leftOver = itemToMove.clone();
        itemToMove.setAmount(1);
        leftOver.setAmount(leftOver.getAmount()-1);
        Bukkit.getScheduler().runTask(main, () -> event.getSource().addItem(leftOver));
        event.setItem(itemToMove);
    }

}
