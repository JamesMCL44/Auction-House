package com.shadebyte.auctionhouse.auction;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Permissions;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:49 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class AuctionPlayer {

    private Player player;

    public AuctionPlayer(Player player) {
        this.player = player;
    }

    public int getTotalActiveAuctions() {
        int total = 0;
        for (AuctionItem item : Core.getInstance().auctionItems) {
            if (item.getOwner().getUniqueId().equals(player.getUniqueId())) {
                total++;
            }
        }
        return total;
    }

    public List<AuctionItem> getAuctionItems() {
        List<AuctionItem> list = new ArrayList<>();
        for (AuctionItem item : Core.getInstance().auctionItems) {
            if (item.getOwner().getUniqueId().equals(player.getUniqueId())) {
                list.add(item);
            }
        }
        return list;
    }

    public boolean hasMaximumAuctionsActive() {
        if (!player.hasPermission(Permissions.MAX_AUCTIONS.getNode() + "." + getTotalActiveAuctions())) {
            return true;
        }
        return false;
    }

    public Player getPlayer() {
        return player;
    }
}
