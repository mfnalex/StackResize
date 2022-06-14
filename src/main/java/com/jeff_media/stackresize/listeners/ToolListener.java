package com.jeff_media.stackresize.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import com.jeff_media.stackresize.BugHandler;
import com.jeff_media.stackresize.StackResize;
import de.jeff_media.jefflib.InventoryUtils;
import de.jeff_media.jefflib.ItemStackUtils;
import de.jeff_media.jefflib.PDCUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;

import java.util.Collections;

public class ToolListener implements Listener {

    private static final StackResize main = StackResize.getInstance();

    @EventHandler
    public void onDurabilityLoss(PlayerItemDamageEvent event) {
        if(event.getDamage() == 0) {
            return;
        }
        ItemStack item = event.getItem();
        //System.out.println(item);
        if(!main.isChangedDangerously(item)) {
            return;
        }
        ItemStack remaining = event.getItem().clone();
        fixCrossbowMeta(remaining);
        remaining.setAmount(remaining.getAmount()-1);

        item.setAmount(1);

        /*Damageable meta = (Damageable) item.getItemMeta();
        PDCUtils.set(meta,"test", DataType.BOOLEAN, true);
        item.setItemMeta(meta);*/


        BugHandler.oneTick(item);

        if(remaining.getAmount() > 0) {
            if(!InventoryUtils.addOrDrop(event.getPlayer(), remaining)) {
                // Send message to player
            }
        }
    }

    private void fixCrossbowMeta(ItemStack remaining) {
        if(remaining.getType() == Material.CROSSBOW) {
            CrossbowMeta meta = (CrossbowMeta) remaining.getItemMeta();
            meta.setChargedProjectiles(Collections.emptyList());
            remaining.setItemMeta(meta);
        }
    }
}
