package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.StackResize;
import com.jeff_media.stackresize.data.EstimatedDestination;
import com.jeff_media.stackresize.data.SpecialInventory;
import com.jeff_media.stackresize.events.ChangedItemMoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ChangedItemMoveEventListener implements Listener {

    private static final StackResize main = StackResize.getInstance();

    @EventHandler
    public void onChangedItemMoveEvent(ChangedItemMoveEvent event) {
        EstimatedDestination estimatedDestination = event.getEstimatedDestination();
        List<Integer> specialSlots = SpecialInventory.getSpecialSlots(event.getView().getTopInventory().getType());
        if(specialSlots.contains(event.getEstimatedDestination().getDestinationSlot())) {
            if((event.getView().getTopInventory().getItem(estimatedDestination.getDestinationSlot()) != null
                    && event.getView().getTopInventory().getItem(estimatedDestination.getDestinationSlot()).getAmount() > 0)
                    || estimatedDestination.getDestinationItem().getAmount() > 1) {
                event.setCancelled(true);
                main.debug("Cancelling ChangedItemMoveEvent");
            }
        }
        //System.out.println(event);
    }
}
