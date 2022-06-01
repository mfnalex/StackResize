package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.StackResize;
import com.jeff_media.stackresize.data.EstimatedDestination;
import com.jeff_media.stackresize.events.ChangedItemMoveEvent;
import de.jeff_media.jefflib.DebugUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChangedItemMoveEventCaller implements Listener {

    private static final StackResize main = StackResize.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        ClickType clickType = event.getClick();
        Inventory clickedInv = event.getClickedInventory();
        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        // The item in the currently clicked slot
        ItemStack currentItem = event.getCurrentItem();

        // The item that already was on the cursor
        ItemStack cursor = event.getCursor();

        Integer hotbarButton = event.getHotbarButton() == -1 ? null : event.getHotbarButton();
        int rawSlot = event.getRawSlot();
        int slot = event.getSlot();

        if(slot < 0 || rawSlot < 0) return;

        InventoryType.SlotType slotType = event.getSlotType();
        boolean isLeft = event.isLeftClick();
        boolean isRight = event.isRightClick();
        boolean isShift = event.isShiftClick();

        //DebugUtils.Events.debug(event);

        EstimatedDestination estimatedDestination = getEstimatedDestination(event);
        if(estimatedDestination == null) {
            main.debug("Ignoring event because EstimatedDestination is null");
            return;
        }

        if(!main.isChangedDangerously(estimatedDestination.getDestinationItem())) {
            main.debug("Ignoring event because the item to be moved wasn't changed dangerously");
            return;
        }

        ChangedItemMoveEvent changedItemMoveEvent = new ChangedItemMoveEvent(event, estimatedDestination);
        Bukkit.getPluginManager().callEvent(changedItemMoveEvent);
        if(changedItemMoveEvent.isCancelled()) {
            event.setCancelled(true);
            main.debug("Cancelling event because ChangedItemMoveEvent was cancelled");
        }
    }

    /**
     * Returns an EstimatedDestination object, or null if we don't have to worry about it
     */
    private static EstimatedDestination getEstimatedDestination(InventoryClickEvent event) {

        Inventory bottomInv = event.getView().getBottomInventory();
        Inventory topInv = event.getView().getTopInventory();
        int hotbarButton = event.getHotbarButton();
        int slot = event.getSlot();

        switch (event.getAction()) {

            // Only affects the cursor, we don't care about that
            case CLONE_STACK:
            case COLLECT_TO_CURSOR:
                return null;

            // We don't care about dropping things
            case DROP_ALL_CURSOR:
            case DROP_ALL_SLOT:
            case DROP_ONE_CURSOR:
            case DROP_ONE_SLOT:
                return null;

            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                return new EstimatedDestination(bottomInv.getItem(hotbarButton),topInv,slot);
        }

        return null;
    }
}
