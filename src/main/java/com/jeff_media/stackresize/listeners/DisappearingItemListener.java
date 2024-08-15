package com.jeff_media.stackresize.listeners;

import com.jeff_media.jefflib.EnumUtils;
import com.jeff_media.jefflib.data.McVersion;
import com.jeff_media.stackresize.BugHandler;
import com.jeff_media.stackresize.StackResize;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

import static com.jeff_media.stackresize.StackResize.is1_21;

public class DisappearingItemListener implements Listener {

    private static final StackResize main = StackResize.getInstance();
    private final Set<Material> noFixConsumables = main.loadRegexList("consumables-no-fix");

    private final Material powderedSnowBucketMaterial = EnumUtils.getIfPresent(Material.class, "POWDER_SNOW_BUCKET").orElse(null);

    private boolean needsFix(ItemStack item) {
        if(item.getAmount() == 1) return false; // Items with 1 amount can always exist.
        if(!main.isChanged(item.getType())) return false; // Unchanged items don't need fixes too
        if(main.getDefaultStackSizes().get(item.getType()) > 1) return false; // Items with a default max stack size above 1 don't need fixes either
        return true; // All other consumables need a fix
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEmptyPowderedSnowBucket(BlockPlaceEvent event) {
        Material type = event.getItemInHand().getType();
        if(type != powderedSnowBucketMaterial) return;
        if(!main.isChanged(type)) return;
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE) return;
        EquipmentSlot slot = event.getHand();
        BugHandler.fixDisappearing(player, slot, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if(is1_21) {

        } else {
            Material type = event.getBucket();
            if (type == powderedSnowBucketMaterial) {
                return; // We listen separately to this
            }
            if (!main.isChanged(type))
                return; // Can NOT use event.getItemStack because that's @Nullable and returns the bucket AFTER the event
            Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) return;
            EquipmentSlot slot = EquipmentSlot.HAND;
            if (player.getInventory().getItemInMainHand().getType() != type) slot = EquipmentSlot.OFF_HAND;
            BugHandler.fixDisappearing(player, slot, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack food = event.getItem();
        Material type = food.getType();
        Player player = event.getPlayer();

        // Fix for milk buckets start - I don't like hardcoding this, but it's the only thing not working
        if(type == Material.MILK_BUCKET && food.getAmount() > 1) {
            BugHandler.give(player, new ItemStack(Material.BUCKET));
            return;
        }
        // Fix for milk buckets end

        if(noFixConsumables.contains(type)) {
            main.debug("don't fix - consumables-no.fix.txt");
            return;
        }
        if(!needsFix(food)) {
            main.debug("don't fix - not needed");
            return;
        }
        EquipmentSlot slot = type == player.getInventory().getItemInMainHand().getType()
                ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        BugHandler.fixDisappearing(player, slot, false);
    }

}
