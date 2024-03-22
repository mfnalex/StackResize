package com.jeff_media.stackresize.data;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@UtilityClass
public class SpecialInventory {

    private static List<Integer> getList(Integer... slots) {
        return Arrays.asList(slots);
    }

    public static List<Integer> getReadonlySlots(InventoryType type) {
        switch(type) {
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
            case SMITHING:
            case CARTOGRAPHY:
            case ANVIL:
            case GRINDSTONE:
                return getList(2);
            case LOOM:
                return getList(3);
            default:
                return new ArrayList<>();
        }
    }

    public static List<Integer> getSpecialSlots(InventoryType type) {
        switch (type) {
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
                return getList(1);
            case ENCHANTING:
            case SMITHING:
            //case BEACON:
            //case CARTOGRAPHY:
                return getList(0);
            case BREWING:
                return getList(0,1,2);
            case ANVIL: // TODO: only return 0 for anvils -> EnchantmentTableListener#onEnchantItemWithStackedEnchantedBook
            case GRINDSTONE:
                return getList(0,1);
            case LOOM:
                return getList(2);
            default:
                return new ArrayList<>();
        }
    }

    public static boolean isSpecialSlot(InventoryType type, int slot) {
        return getSpecialSlots(type).contains(slot);
    }

    public static boolean isAllowed(InventoryType type, int slot, Material mat) {
        return true;
    }

}
