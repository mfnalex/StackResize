package com.jeff_media.stackresize;

import com.jeff_media.morepersistentdatatypes.DataType;
import com.jeff_media.jefflib.JeffLib;
import com.jeff_media.jefflib.PDCUtils;
import com.jeff_media.jefflib.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class BugHandler {

    private static final StackResize main = StackResize.getInstance();

    private static final NamespacedKey DUMMY_KEY = PDCUtils.getKey("unique-id");

    public static void oneTick(ItemStack stack) {
        if(stack.getAmount() == 0) return;
        ItemMeta meta = stack.getItemMeta();
        if(meta == null) return;
        PDCUtils.set(meta, DUMMY_KEY, PersistentDataType.LONG, JeffLib.getRandom().nextLong());
        stack.setItemMeta(meta);
        Tasks.nextTick(() -> {
            if(stack.hasItemMeta()) {
                ItemMeta meta2 = stack.getItemMeta();
                PDCUtils.remove(meta2, DUMMY_KEY);
                stack.setItemMeta(meta2);
            }
        });
    }

    public static void reduce(ItemStack item) {
        item.setAmount(item.getAmount()-1);
    }

//    public static void fixDisappearing(Player player, EquipmentSlot slot, boolean reduce, ItemStack item) {
//        //ItemStack item = player.getInventory().getItem(slot);
//        main.debug("Fix disappearing: " + item);
//        if(reduce) reduce(item); // Buckets have to be reduced manually. Mushroom stews don't.
//        Bukkit.getScheduler().runTask(main, () -> {
//            ItemStack replacement = player.getInventory().getItem(slot);
//            player.getInventory().setItem(slot, item);
//            if(replacement.getAmount()>0) {
//                give(player, replacement);
//            }
//        });
//    }

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
