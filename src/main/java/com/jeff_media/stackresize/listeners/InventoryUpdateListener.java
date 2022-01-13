package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public class InventoryUpdateListener implements Listener {

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Bukkit.broadcastMessage("asd");
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(!Config.isUpdateInventories()) return;
        ((Player)event.getWhoClicked()).updateInventory();
    }
}
