package com.kiranhart.auctionhouse.inventory.inventories;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:09 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.google.common.collect.Lists;
import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.api.version.NBTEditor;
import com.kiranhart.auctionhouse.api.version.XMaterial;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.auction.AuctionPlayer;
import com.kiranhart.auctionhouse.inventory.AGUI;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AuctionGUI implements AGUI {

    private Player p;
    private List<List<AuctionItem>> chunks;

    public AuctionGUI(Player p) {
        this.p = p;
        chunks = Lists.partition(Core.getInstance().getAuctionItems(), 45);
    }

    private int page = 1;

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        /*
        Page navigation system
         */

        try {
            if (page >= 1 && slot == 48) p.openInventory(setPage(this.getPage() - 1).getInventory());
            if (page >= 1 && slot == 50) p.openInventory(setPage(this.getPage() + 1).getInventory());
        } catch (Exception ex) {
            Debugger.report(ex, false);
        }

        //Different auction inventories
        //Refresh Auction GUI
        if (slot == 49) {

            return;
        }

        //Open Listings GUI
        if (slot == 45) {

            return;
        }

        //Open Expired GUI
        if (slot == 46) {

            return;
        }

        //Open Transaction Selection GUI
        if (slot == 51) {

            return;
        }

        //Clicking on active auction items.
        //Check if air
        if (clicked == null || clicked.getType() == XMaterial.AIR.parseMaterial()) {
            return;
        }

        AuctionItem possibleAuctionItem = null;

        /*
        Perform the proper steps if the user left-clicks (using bid system)
         */
        if (e.getClick() == ClickType.LEFT && AuctionSettings.USE_BIDDING_SYSTEM) {

            //Get the key of the auction item
            String auctionItemKey = NBTEditor.getString(clicked, "AuctionItemKey");
            for (AuctionItem auctionItem : Core.getInstance().getAuctionItems()) {
                if (auctionItem.getKey().equalsIgnoreCase(auctionItemKey)) possibleAuctionItem = auctionItem;
            }

            //Check if player has enough money to bid
            if (Core.getInstance().getEconomy().hasBalance(p, possibleAuctionItem.getCurrentPrice() + possibleAuctionItem.getBidIncrement())) {
                //Check if the person who clicked is the owner
                if (possibleAuctionItem.getOwner().equals(p.getUniqueId())) {
                    //can the owner bid on their own item?
                    if (AuctionSettings.OWNER_CAN_BID_ON_OWN) {
                        //Update the price
                        possibleAuctionItem.setCurrentPrice(possibleAuctionItem.getCurrentPrice() + possibleAuctionItem.getBidIncrement());
                        //Alert the previous bidder someone has a higher bid than them
                        if (!possibleAuctionItem.getHighestBidder().equals(p.getUniqueId())) {
                            Core.getInstance().getLocale().getMessage(AuctionLang.OUT_BIDDED).processPlaceholder("player", p.getName()).sendPrefixedMessage(Bukkit.getOfflinePlayer(possibleAuctionItem.getHighestBidder()).getPlayer());
                        }
                        //Set the highest bidder
                        possibleAuctionItem.setHighestBidder(p.getUniqueId());
                    } else {
                        //Owner cannot bid on own item.
                        Core.getInstance().getLocale().getMessage(AuctionLang.CANT_BID_ON_OWN).sendPrefixedMessage(p);
                    }
                } else {
                    //Clicked user is not the original owner
                    //Update the price
                    possibleAuctionItem.setCurrentPrice(possibleAuctionItem.getCurrentPrice() + possibleAuctionItem.getBidIncrement());
                    //Alert the previous bidder someone has a higher bid than them
                    if (!possibleAuctionItem.getHighestBidder().equals(p.getUniqueId())) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.OUT_BIDDED).processPlaceholder("player", p.getName()).sendPrefixedMessage(Bukkit.getOfflinePlayer(possibleAuctionItem.getHighestBidder()).getPlayer());
                    }
                    //Set the highest bidder
                    possibleAuctionItem.setHighestBidder(p.getUniqueId());
                }

                //Increase time on bid?
                if (AuctionSettings.INCREASE_AUCTION_TIME_ON_BID) {
                    possibleAuctionItem.setTime(possibleAuctionItem.getTime() + AuctionSettings.TIME_TO_INCREASE_BY_BID);
                }

            } else {
                //Not enough money to bid
                e.getClickedInventory().setItem(slot, AuctionAPI.getInstance().createNotEnoughMoneyIcon());
                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Core.getInstance(), () -> {
                    p.closeInventory();
                    p.openInventory(new AuctionGUI(p).getInventory());
                }, 20);
            }

            p.closeInventory();
            p.openInventory(new AuctionGUI(p).getInventory());
            return;
        }

        /*
        Perform the proper steps if the user right-clicks (using bid system)
         */
        if (e.getClick() == ClickType.RIGHT && AuctionSettings.USE_BIDDING_SYSTEM) {

            //Get the key of the auction item
            String auctionItemKey = NBTEditor.getString(clicked, "AuctionItemKey");
            for (AuctionItem auctionItem : Core.getInstance().getAuctionItems()) {
                if (auctionItem.getKey().equalsIgnoreCase(auctionItemKey)) possibleAuctionItem = auctionItem;
            }

            if (Core.getInstance().getEconomy().hasBalance(p, possibleAuctionItem.getBuyNowPrice())) {
                if (AuctionSettings.OWNER_CAN_PURCHASE_OWN) {
                    p.closeInventory();
                    p.openInventory(new ConfirmationGUI().getInventory());
                } else {
                    Core.getInstance().getLocale().getMessage(AuctionLang.CANT_BUY_OWN).sendPrefixedMessage(p);
                }
            } else {
                //Not enough money to purchase
                e.getClickedInventory().setItem(slot, AuctionAPI.getInstance().createNotEnoughMoneyIcon());
                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Core.getInstance(), () -> {
                    p.closeInventory();
                    p.openInventory(new AuctionGUI(p).getInventory());
                }, 20);
            }

            p.closeInventory();
            p.openInventory(new AuctionGUI(p).getInventory());
            return;
        }

        /*
        Perform the proper steps if the user left clicks (Without the bid system)
         */
        if (e.getClick() == ClickType.LEFT && !AuctionSettings.USE_BIDDING_SYSTEM) {

            //Get the key of the auction item
            String auctionItemKey = NBTEditor.getString(clicked, "AuctionItemKey");
            for (AuctionItem auctionItem : Core.getInstance().getAuctionItems()) {
                if (auctionItem.getKey().equalsIgnoreCase(auctionItemKey)) possibleAuctionItem = auctionItem;
            }

            if (Core.getInstance().getEconomy().hasBalance(p, possibleAuctionItem.getBuyNowPrice())) {
                if (AuctionSettings.OWNER_CAN_PURCHASE_OWN) {
                    p.closeInventory();
                    p.openInventory(new ConfirmationGUI().getInventory());
                } else {
                    Core.getInstance().getLocale().getMessage(AuctionLang.CANT_BUY_OWN).sendPrefixedMessage(p);
                }
            } else {
                //Not enough money to purchase
                e.getClickedInventory().setItem(slot, AuctionAPI.getInstance().createNotEnoughMoneyIcon());
                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Core.getInstance(), () -> {
                    p.closeInventory();
                    p.openInventory(new AuctionGUI(p).getInventory());
                }, 20);
            }

            p.closeInventory();
            p.openInventory(new AuctionGUI(p).getInventory());
            return;
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.auctionhouse.title")));

        inventory.setItem(45, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.yourauctions", new AuctionPlayer(p).getTotalActiveAuctions(), 0));
        inventory.setItem(46, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.collectionbin", 0, new AuctionPlayer(p).getTotalExpiredAuctions()));
        inventory.setItem(48, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.previouspage", 0, 0));
        inventory.setItem(49, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.refresh", 0, 0));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.nextpage", 0, 0));
        inventory.setItem(51, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.transactions", 0, 0));
        inventory.setItem(52, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.howtosell", 0, 0));
        inventory.setItem(53, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.guide", 0, 0));

        //Pagination
        if (chunks.size() != 0) {
            chunks.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item.getAuctionStack(AuctionItem.AuctionItemType.MAIN)));
        }
        return inventory;
    }

    @Override
    public void close(InventoryCloseEvent e) {
    }

    public AuctionGUI setPage(int page) {
        if (page <= 0)
            this.page = 1;
        else
            this.page = page;
        return this;
    }

    public int getPage() {
        return page;
    }
}