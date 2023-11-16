package com.jeff_media.stackresize.listeners;

import com.jeff_media.jefflib.Tasks;
import com.jeff_media.stackresize.BugHandler;
import com.jeff_media.stackresize.StackResize;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JukeboxListener implements Listener {

    private static final Set<Material> MUSIC_DISCS = new HashSet<>();
    private final StackResize plugin = StackResize.getInstance();

    static {
        for(Material mat : Material.values()) {
            if(mat.name().startsWith("MUSIC_DISC_")) {
                //System.out.println("Adding " + mat.name() + " to MUSIC_DISCS");
                MUSIC_DISCS.add(mat);
            }
        }
    }

    @EventHandler
    public void onJukebox(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            //System.out.println("Not right click block");
            return;
        }
        Block block = event.getClickedBlock();
        if(block == null) {
            //System.out.println("Block is null");
            return;
        }
        if(block.getType() != Material.JUKEBOX) {
            //System.out.println("Block is not jukebox");
            return;
        }
        ItemStack item = event.getItem();
        if(item == null) {
            //System.out.println("Item is null");
            return;
        }
        item = item;
        if(!plugin.isChangedDangerously(item.getType())) {
            //System.out.println("Item is not changed");
            return;
        }
        if(!MUSIC_DISCS.contains(item.getType())) {
            //System.out.println("Item is not music disc");
            return;
        }
        Jukebox jukebox = (Jukebox) block.getState();
        //System.out.println("Record: " + jukebox.getRecord());
        if(jukebox.getRecord() == null || jukebox.getRecord().getType().isAir()) {
            //System.out.println("Jukebox is already empty");
        } else {
            //System.out.println("Returning, Jukebox is not empty");
            return;
        }
        event.setCancelled(true);
        ItemStack finalItem = item.clone();
        BugHandler.reduce(item);
        Tasks.nextTick(() -> {
            //System.out.println("Setting record");
//            if(jukebox.getRecord() == null) {
//                //System.out.println("Record is null");
//                return;
//            }
//            if(jukebox.getRecord().getAmount() < 1) {
//                //System.out.println("Record amount is less than 1");
//                return;
//            }
            finalItem.setAmount(1);
            jukebox.setRecord(finalItem);
            jukebox.update();
            //System.out.println("Record set: " + jukebox.getRecord() + ", " + finalItem);
        });
    }
}
