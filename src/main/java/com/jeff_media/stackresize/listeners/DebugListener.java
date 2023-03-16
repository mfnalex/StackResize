package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.StackResize;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DebugListener implements Listener {

    private static final StackResize main = StackResize.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!main.getConfig().getBoolean("verbose",false)) return;
        main.debug("=== InventoryClickEvent Start ===");
        main.debug("Slot: " + event.getSlot());
        main.debug("Raw slot: " + event.getRawSlot());
        main.debug("=== InventoryClickEvent End   ===");
    }
}
