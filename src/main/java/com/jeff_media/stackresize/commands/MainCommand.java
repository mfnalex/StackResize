package com.jeff_media.stackresize.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.jeff_media.stackresize.config.Messages;
import com.jeff_media.stackresize.StackResize;
import com.jeff_media.jefflib.CommandUtils;
import com.jeff_media.jefflib.EnumUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Stack;

import static com.jeff_media.stackresize.StackResize.is1_21;

@CommandAlias("stackresize")
@CommandPermission("stackresize.admin")
public class MainCommand extends BaseCommand {

    private static final StackResize main = StackResize.getInstance();

    @HelpCommand
    @Default
    public static void onHelp(CommandSender sender) {
        sender.sendMessage(new String[] {Messages.getHeader(),""}); // No varargs in 1.16.5
        CommandUtils.sendHelpMessage(sender, CommandUtils.HelpStyle.SAME_LINE_SPACED,
                "§6/stackresize reload","§7Reloads the configuration",
                "§6/stackresize info [item]","§7Display information about the item in your main hand, or the specified item",
                "§6/stackresize set <amount> [item]","§7Sets the max stack size of the item in your main hand, or the specified item"
                );
    }

    private static int toValidStackSize(String value) {
        try {
            int result = Integer.parseInt(value);
            if(result > 0 && result < StackResize.MAX_STACK_SIZE) return result;
        } catch (NumberFormatException ignored) {

        }
        return -1;
    }

    // DEBUG START
    @Subcommand("hunger")
    public static void hunger(Player player) {
        player.setFoodLevel(5);
    }

    @Subcommand("debug")
    public static void debug(CommandSender sender) {
        boolean newValue = !main.getConfig().getBoolean("debug");
        main.getConfig().set("debug", newValue );
        sender.sendMessage("StackResize debug mode " + (newValue ? "enabled" : "disabled"));
    }
    // DEBUG END

    @Subcommand("info")
    @CommandCompletion("@materials")
    public static void info(CommandSender sender, String[] args) {
        Material mat = null;
        if(sender instanceof Player) {
            mat = ((Player)sender).getInventory().getItemInMainHand().getType();
        }
        if(args.length>0) {
            mat = EnumUtils.getIfPresent(Material.class,args[0].toUpperCase(Locale.ROOT)).orElse(null);
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
        if(isUnsupported(sender, false)) return;
        main.loadConfigAndSetStackSizes();
        sender.sendMessage(Messages.reloaded());
    }

    @Subcommand("set")
    @CommandCompletion("@range:1-99 @materials")
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
            material = EnumUtils.getIfPresent(Material.class, args[1].toUpperCase(Locale.ROOT)).orElse(null);
            if(material == null) {
                sender.sendMessage(Messages.invalidMaterial(args[1]));
                return;
            }
        }

        if(main.isForcefullyUnstackable(material)) {
            sender.sendMessage(Messages.cantBeStacked(material));
            return;
        }

        int validAmount = toValidStackSize(args[0]);
        if(validAmount == -1) {
            sender.sendMessage(Messages.notAnInt(args[0]));
            return;
        }
        sender.sendMessage(Messages.setSize(material,validAmount));

        if(isUnsupported(sender, true)) return;

        main.changeStackSize(material,validAmount);
    }

    private static boolean isUnsupported(CommandSender sender, boolean changedFile) {
        if(is1_21) {
            if (changedFile) {
                sender.sendMessage("§eChanges have been saved to the config file, but will not take effect until the server is restarted.");
            } else {
                sender.sendMessage("§eChanging stack sizes while the server is running is not supported in 1.20.5+. Please restart your server to apply changes.");
            }
            return true;
        }
        return false;
    }
}
