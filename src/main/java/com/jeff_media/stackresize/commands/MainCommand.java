package com.jeff_media.stackresize.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.base.Enums;
import com.jeff_media.stackresize.config.Messages;
import com.jeff_media.stackresize.StackResize;
import de.jeff_media.jefflib.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

@CommandAlias("stackresize")
@CommandPermission("stackresize.admin")
public class MainCommand extends BaseCommand {

    private static final StackResize main = StackResize.getInstance();

    @HelpCommand
    @Default
    public static void onHelp(CommandSender sender) {
        sender.sendMessage(Messages.getHeader(),"");
        CommandUtils.sendHelpMessage(sender, CommandUtils.HelpStyle.SAME_LINE_SPACED,
                "§6/stackresize info [item]","§7Display information about the item in your main hand, or the specified item",
                "§6/stackresize reload","§7Reloads the configuration",
                "§6/stackresize set <amount> [item]","§7Sets the max stack size of the item in your main hand, or the specified item"
                );
    }

    private static int toValidStackSize(String value) {
        try {
            int result = Integer.parseInt(value);
            if(result > 0 && result < 65) return result;
        } catch (NumberFormatException ignored) {

        }
        return -1;
    }

    @Subcommand("hunger")
    public static void hunger(Player player) {
        player.setFoodLevel(5);
    }

    @Subcommand("info")
    @CommandCompletion("@materials")
    public static void info(CommandSender sender, String[] args) {
        Material mat = null;
        if(sender instanceof Player) {
            mat = ((Player)sender).getInventory().getItemInMainHand().getType();
        }
        if(args.length>0) {
            mat = Enums.getIfPresent(Material.class,args[0].toUpperCase(Locale.ROOT)).orNull();
            if(mat == null) {
                sender.sendMessage(Messages.invalidMaterial(args[0]));
                return;
            }
        }
        if(mat == null || mat.isAir()) {
            sender.sendMessage(Messages.noItemInHand());
            return;
        }
        sender.sendMessage(Messages.getHeader()+"\n");
        sender.sendMessage(Messages.getInfo(mat));
    }

    @Subcommand("reload")
    public static void reload(CommandSender sender) {
        main.loadConfigAndSetStackSizes();
        sender.sendMessage(Messages.reloaded());
    }

    @Subcommand("set")
    @CommandCompletion("@range:1-64 @materials")
    public static void setStackSize(CommandSender sender, String[] args) {
        if(args.length==0) {
            sender.sendMessage(Messages.noAmountSpecified());
        }
        Material material;
        if(args.length<2) {
            if(sender instanceof Player) {
                ItemStack item = ((Player)sender).getInventory().getItemInMainHand();
                if (item.getAmount() == 0 || item.getType().isAir()) {
                    sender.sendMessage(Messages.noItemInHand());
                    return;
                }
                material = item.getType();
            } else {
                sender.sendMessage(Messages.noMaterialSpecified());
                return;
            }
        } else {
            material = Enums.getIfPresent(Material.class, args[1].toUpperCase(Locale.ROOT)).orNull();
            if(material == null) {
                sender.sendMessage(Messages.invalidMaterial(args[1]));
                return;
            }
        }

        if(main.getUnstackableTools().contains(material)) {
            sender.sendMessage(Messages.cantBeStacked(material));
            return;
        }

        int validAmount = toValidStackSize(args[0]);
        if(validAmount == -1) {
            sender.sendMessage(Messages.notAnInt(args[0]));
            return;
        }
        sender.sendMessage(Messages.setSize(material,validAmount));
        main.changeStackSize(material,validAmount);
    }
}
