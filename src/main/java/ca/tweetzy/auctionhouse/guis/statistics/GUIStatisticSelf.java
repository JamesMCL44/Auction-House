/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.guis.statistics;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;

import java.util.HashMap;

public final class GUIStatisticSelf extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;

	public GUIStatisticSelf(AuctionPlayer player) {
		super(player);
		this.auctionPlayer = player;
		setTitle(Settings.GUI_STATS_SELF_TITLE.getString());
		setDefaultItem(ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_SELF_BG_ITEM.getString()));
		setUseLockedCells(true);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(6);
		draw();
	}

	private void draw() {
		final AuctionHouse instance = AuctionHouse.getInstance();

		// created auction
		setItem(1, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_SELF_ITEMS_CREATED_AUCTION_ITEM.getString(), Settings.GUI_STATS_SELF_ITEMS_CREATED_AUCTION_NAME.getString(), Settings.GUI_STATS_SELF_ITEMS_CREATED_AUCTION_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%created_auctions%", (int) instance.getAuctionStatisticManager().getStatisticByPlayer(player.getUniqueId(), AuctionStatisticType.CREATED_AUCTION));
		}}));

		// sold auction
		setItem(3, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_SELF_ITEMS_SOLD_AUCTION_ITEM.getString(), Settings.GUI_STATS_SELF_ITEMS_SOLD_AUCTION_NAME.getString(), Settings.GUI_STATS_SELF_ITEMS_SOLD_AUCTION_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%sold_auctions%", (int) instance.getAuctionStatisticManager().getStatisticByPlayer(player.getUniqueId(), AuctionStatisticType.SOLD_AUCTION));
		}}));

		// created bin
		setItem(1, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_SELF_ITEMS_CREATED_BIN_ITEM.getString(), Settings.GUI_STATS_SELF_ITEMS_CREATED_BIN_NAME.getString(), Settings.GUI_STATS_SELF_ITEMS_CREATED_BIN_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%created_bins%", (int) instance.getAuctionStatisticManager().getStatisticByPlayer(player.getUniqueId(), AuctionStatisticType.CREATED_BIN));
		}}));

		// sold bin
		setItem(3, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_SELF_ITEMS_SOLD_BIN_ITEM.getString(), Settings.GUI_STATS_SELF_ITEMS_SOLD_BIN_NAME.getString(), Settings.GUI_STATS_SELF_ITEMS_SOLD_BIN_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%sold_bins%", (int) instance.getAuctionStatisticManager().getStatisticByPlayer(player.getUniqueId(), AuctionStatisticType.SOLD_BIN));
		}}));

		// money earned
		setItem(1, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_SELF_ITEMS_MONEY_EARNED_ITEM.getString(), Settings.GUI_STATS_SELF_ITEMS_MONEY_EARNED_NAME.getString(), Settings.GUI_STATS_SELF_ITEMS_MONEY_EARNED_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%money_earned%", AuctionAPI.getInstance().formatNumber(instance.getAuctionStatisticManager().getStatisticByPlayer(player.getUniqueId(), AuctionStatisticType.MONEY_EARNED)));
		}}));

		// money spent
		setItem(3, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_SELF_ITEMS_MONEY_SPENT_ITEM.getString(), Settings.GUI_STATS_SELF_ITEMS_MONEY_SPENT_NAME.getString(), Settings.GUI_STATS_SELF_ITEMS_MONEY_SPENT_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%money_spent%", AuctionAPI.getInstance().formatNumber(instance.getAuctionStatisticManager().getStatisticByPlayer(player.getUniqueId(), AuctionStatisticType.MONEY_SPENT)));
		}}));

		setButton(5, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIStatisticViewSelect(this.auctionPlayer));
		});
	}
}
