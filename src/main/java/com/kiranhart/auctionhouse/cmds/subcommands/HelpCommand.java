package com.kiranhart.auctionhouse.cmds.subcommands;

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.cmds.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:51 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class HelpCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(AuctionPermissions.HELP_COMMNAD)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.NO_PERMISSION).sendPrefixedMessage(sender);
            return;
        }

        Core.getInstance().getConfig().getStringList("help-msg").forEach(line -> {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        });

        if (AuctionAPI.getInstance().senderHasHigherPermissions(sender)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c/ah reload"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c/ah lock"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c/ah endall"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c/ah uploadtransactions"));
        }
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().help;
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }

}
