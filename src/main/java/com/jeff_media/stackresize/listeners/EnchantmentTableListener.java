package com.jeff_media.stackresize.listeners;

import com.jeff_media.stackresize.StackResize;
import de.jeff_media.jefflib.MaterialUtils;
import de.jeff_media.jefflib.Tasks;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnchantmentTableListener implements Listener {

    private static final StackResize main = StackResize.getInstance();

    private final Set<Material> stackSizes = new HashSet<>();

    @EventHandler
    public void onInsertToEnchantmentTable(PrepareItemEnchantEvent event) {
        if(!main.isChangedDangerously(event.getItem())) return;
        Material material = event.getItem().getType();
        if(stackSizes.contains(material)) return;
        stackSizes.add(material);
        int stackSize = material.getMaxStackSize();
        MaterialUtils.setMaxStackSize(material,1);
        Tasks.nextTick(() -> {
            MaterialUtils.setMaxStackSize(material,stackSize);
            stackSizes.remove(material);
        });
    }

}
