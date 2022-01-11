package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.BugHandler;
import com.jeff_media.stackresize.StackResize;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class DisappearingItemListener implements Listener {

    private static final StackResize main = StackResize.getInstance();
    private final Set<Material> noFixConsumables = main.loadRegexList("consumables-no-fix");

    private boolean needsFix(ItemStack item) {
        if(item.getAmount() == 1) return false; // Items with 1 amount can always exist.
        if(!main.isChanged(item.getType())) return false; // Unchanged items don't need fixes too
        if(main.getDefaultStackSizes().get(item.getType()) > 1) return false; // Items with a default max stack size above 1 don't need fixes either
        return true; // All other consumables need a fix
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Material type = event.getBucket();
        if(!main.isChanged(type)) return; // Can NOT use event.getItemStack because that's @Nullable and returns the bucket AFTER the event
        Player player = event.getPlayer();
        EquipmentSlot slot = EquipmentSlot.HAND;
        if(player.getInventory().getItemInMainHand().getType() != type) slot = EquipmentSlot.OFF_HAND;
        BugHandler.fixDisappearing(player, slot, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        if(true) return;
        ItemStack food = event.getItem();
        Material type = food.getType();
        if(noFixConsumables.contains(type)) return;
        if(!needsFix(food)) return;
        Player player = event.getPlayer();
        EquipmentSlot slot = type == player.getInventory().getItemInMainHand().getType()
                ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        BugHandler.fixDisappearing(player, slot, false);
    }

}
