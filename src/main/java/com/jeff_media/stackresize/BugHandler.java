package com.jeff_media.stackresize;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BugHandler {

    private static final StackResize main = StackResize.getInstance();

    public static void reduce(ItemStack item) {
        item.setAmount(item.getAmount()-1);
    }

    public static void fixDisappearing(Player player, EquipmentSlot slot, boolean reduce) {
        ItemStack item = player.getInventory().getItem(slot);
        main.debug("Fix disappearing: " + item);
        if(reduce) reduce(item); // Buckets have to be reduced manually. Mushroom stews don't.
        Bukkit.getScheduler().runTask(main, () -> {
           ItemStack replacement = player.getInventory().getItem(slot);
           player.getInventory().setItem(slot, item);
           if(replacement.getAmount()>0) {
               give(player, replacement);
           }
        });
    }

    private static void dropRemaining(HashMap<Integer,ItemStack> leftover, Location location) {
        for(ItemStack item : leftover.values()) {
            location.getWorld().dropItemNaturally(location, item);
        }
    }

    public static void give(Player player, ItemStack item) {
        dropRemaining(player.getInventory().addItem(item),player.getLocation());
    }

}
