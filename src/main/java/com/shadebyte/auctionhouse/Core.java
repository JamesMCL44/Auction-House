package com.shadebyte.auctionhouse;

import com.massivestats.MassiveStats;
import com.shadebyte.auctionhouse.auction.AuctionItem;
import com.shadebyte.auctionhouse.cmds.CommandManager;
import com.shadebyte.auctionhouse.events.AGUIListener;
import com.shadebyte.auctionhouse.util.ConfigWrapper;
import com.shadebyte.auctionhouse.util.Debugger;
import com.shadebyte.auctionhouse.util.Locale;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Core extends JavaPlugin {

    //Instance Variable
    private static Core instance;

    //Instance for the command management system
    private CommandManager commandManager;

    //Economy
    private static Economy economy = null;

    //Settings
    private Settings settings = null;

    //Data config instance from the config wrapper
    private ConfigWrapper data;

    //Language system instance
    private Locale locale;

    //Storage
    public List<AuctionItem> auctionItems;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        //Locales
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(this.getConfig().getString("Locale", "en_US"));

        settings = new Settings();

        initDataFiles();

        commandManager = new CommandManager();
        commandManager.initialize();

        initEvents();
        initStorage();

        try {
            MassiveStats stats = new MassiveStats(this);
            stats.setListenerDisabled(false);
        } catch (Exception e) {
            Debugger.report(e);
        }

        try {
            Bukkit.getServer().getScheduler().runTaskTimer(this, () -> {
                auctionItems.forEach(auctionItem -> auctionItem.updateTime(5));
                auctionItems.forEach(auctionItem -> {
                    if (auctionItem.getTime() <= 0) auctionItems.remove(auctionItem);
                });
            }, 0, 20 * 5);
        } catch (Exception e) {
            //Debugger.report(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void initDataFiles() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        data = new ConfigWrapper(this, "", "data.yml");
        if (!new File(this.getDataFolder(), "data.yml").exists()) {
            data.getConfig().createSection("data");
        }
        data.saveConfig();
    }

    private void initEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AGUIListener(), this);
    }

    private void initStorage() {
        auctionItems = new ArrayList<>();
    }

    public static Core getInstance() {
        return instance;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public ConfigWrapper getData() {
        return data;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public Settings getSettings() {
        return settings;
    }
}
