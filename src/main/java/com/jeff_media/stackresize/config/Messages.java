package com.jeff_media.stackresize.config;

import com.jeff_media.jefflib.data.McVersion;
import com.jeff_media.stackresize.StackResize;
import com.jeff_media.jefflib.MaterialUtils;
import com.jeff_media.jefflib.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Messages {

    private static final StackResize main = StackResize.getInstance();

    private static final String GRADIENT = TextUtils.format("<#fb0000>&lS<#fb1901>&lt<#fa3303>&la<#fa4c04>&lc<#fa6506>&lk<#fa7f07>&lR<#f99808>&le<#f9b10a>&ls<#f9ca0b>&li<#f8e40d>&lz<#f8fd0e>&le");
    private static final String PREFIX = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[ " + ChatColor.GOLD + GRADIENT + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ] " + ChatColor.RESET;
    private static final String HEADER = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "=  =  =  =  =  [ " + GRADIENT + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ]  =  =  =  =  =";

    public static String setSize(Material material, int amount) {
        return (PREFIX + ChatColor.GREEN + "Set max stack size of " + ChatColor.DARK_GREEN + "{mat}" + ChatColor.GREEN + " to " + ChatColor.GOLD + "{amount}")
                .replace("{mat}", MaterialUtils.getNiceMaterialName(material))
                .replace("{amount}",String.valueOf(amount));
    }

    public static String notAnInt(String value) {
        return (PREFIX + ChatColor.DARK_RED + "{value}" + ChatColor.RED + " is not a valid number between 1 and 64")
                .replace("{value}",value);
    }

    public static String noItemInHand() {
        return (PREFIX + ChatColor.RED + "You must hold an item in your main hand or specify a material.");
    }

    public static String noAmountSpecified() {
        return (PREFIX + ChatColor.RED + "You must specify an amount.");
    }

    public static String noMaterialSpecified() {
        return (PREFIX + ChatColor.RED + "You must specify a material when running this command from console.");
    }

    public static String invalidMaterial(String value) {
        return (PREFIX + ChatColor.DARK_RED + "{value}" + ChatColor.RED + " is not a valid material.")
                .replace("{value}",value);
    }

    public static String reloaded() {
        return (PREFIX + ChatColor.GREEN + "Configuration has been reloaded.");
    }

    public static String cantBeStacked(Material mat) {
        return (PREFIX + ChatColor.RED + "Sorry, but " + ChatColor.DARK_GREEN + "{value}" + ChatColor.RED+" can't be made stackable.")
                .replace("{value}",mat.name());
    }

    public static String getHeader() {
        return HEADER;
    }

    public static String[] getInfo(Material mat) {
        int stackSize = mat.getMaxStackSize();
        int defaultStackSize = main.getDefaultStackSizes().get(mat);
        int defaultItemStackSize = new ItemStack(mat).getMaxStackSize();
        ItemMeta meta = new ItemStack(mat).getItemMeta();
        String defaultMetaStackSize = meta.hasMaxStackSize() ? meta.getMaxStackSize() + "" : "N/A";
        String info = "Mat#getMaxStackSize: " + stackSize + "\n" +
                "Default stack size: " + defaultStackSize + "\n" +
                "Default item stack size: " + defaultItemStackSize + "\n" +
                "Default meta stack size: " + defaultMetaStackSize;
        System.out.println("\n\n" + info + "\n\n");
        String defaultValue = "&7Unchanged (" + defaultStackSize + ")";
        String changedValue = "&6" + stackSize + "&7 (Default: " + defaultStackSize + ")";
        String[] msg = {
                "&7Material: &6{material}".replace("{material}",MaterialUtils.getNiceMaterialName(mat)),
                "&7Max Stack size: {value}".replace("{value}", stackSize == defaultStackSize ? defaultValue : changedValue)
        };
        return translateColorCodes(msg);
    }

    private static String[] translateColorCodes(String... msg) {
        return Arrays.stream(msg).map(line -> ChatColor.translateAlternateColorCodes('&', fix1_15ColorCodes(line))).toArray(String[]::new);
    }

    private static String fix1_15ColorCodes(String msg) {
        if(McVersion.current().isAtLeast(1,16)) {
            return msg;
        }
        return msg.replaceAll("&x&[a-zA-z0-9]&[a-zA-z0-9]&[a-zA-z0-9]&[a-zA-z0-9]&[a-zA-z0-9]&[a-zA-z0-9]", "&b");
    }
}
