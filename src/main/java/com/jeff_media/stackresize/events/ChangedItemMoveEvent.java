package com.jeff_media.stackresize.events;

import com.jeff_media.stackresize.data.EstimatedDestination;
import com.jeff_media.stackresize.listeners.ChangedItemMoveEventCaller;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an InventoryClickEvent that moves a dangerously changed ItemStack (MaxStackSize > DefaultMaxStackSize)
 */
@ToString
public class ChangedItemMoveEvent extends InventoryClickEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter private final EstimatedDestination estimatedDestination;
    private boolean cancelled = false;

    public ChangedItemMoveEvent(InventoryClickEvent clickEvent, @NonNull EstimatedDestination estimatedDestination) {
        super(clickEvent.getView(), clickEvent.getSlotType(), clickEvent.getSlot(), clickEvent.getClick(),clickEvent.getAction(), clickEvent.getHotbarButton());
        this.estimatedDestination = estimatedDestination;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean toCancel) {
        cancelled = toCancel;
    }

    //@DoNotRename
    public static HandlerList getHandlerList(){
        return HANDLERS;
    }

    @Override
    //@DoNotRename
    public final HandlerList getHandlers(){
        return HANDLERS;
    }

}
