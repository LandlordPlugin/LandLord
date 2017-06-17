package com.jcdesimp.landlord;

import biz.princeps.lib.storage.AbstractDatabase;
import biz.princeps.lib.storage.SQLite;
import com.jcdesimp.landlord.configuration.CustomConfig;
import com.jcdesimp.landlord.landFlags.*;
import com.jcdesimp.landlord.landManagement.FlagManager;
import com.jcdesimp.landlord.landManagement.LandManager;
import com.jcdesimp.landlord.landManagement.ViewManager;
import com.jcdesimp.landlord.landMap.MapManager;
import com.jcdesimp.landlord.persistantData.db.MySQLDatabase;
import com.jcdesimp.landlord.persistantData.db.SQLiteDatabase;
import com.jcdesimp.landlord.pluginHooks.VaultHandler;
import com.jcdesimp.landlord.pluginHooks.WorldguardHandler;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Landlord
 */
public final class Landlord extends JavaPlugin {

    private AbstractDatabase db;
    private Landlord plugin;
    private MapManager mapManager;
    private WorldguardHandler wgHandler;
    private VaultHandler vHandler;
    private FlagManager flagManager;
    private ViewManager manageViewManager;
    private LandAlerter pListen;
    private LandManager landManager;

    private CustomConfig mainConfig;
    private CustomConfig messagesConfig;

    public static Landlord getInstance() {
        return (Landlord) Bukkit.getPluginManager().getPlugin("Landlord");
        //return Bukkit.getPluginManager().getPlugin("MyPlugin");
    }

    @Override
    public void onEnable() {
        plugin = this;
        mapManager = new MapManager(this);
        //listner = new LandListener();
        //getServer().getPluginManager().registerEvents(new LandListener(this), this);
        flagManager = new FlagManager(this);
        manageViewManager = new ViewManager();
        getServer().getPluginManager().registerEvents(mapManager, this);


        // generate/load the main config file
        mainConfig = new CustomConfig(this, "config.yml", "config.yml");
        // generate/load the main language file based on language value in config.
        messagesConfig = new CustomConfig(this, "messages/english.yml", "messages/" + (mainConfig.get().getString("options.messagesFile").replace("/", ".")));
        // Registering Alert Listener
        pListen = new LandAlerter(plugin);
        if (getConfig().getBoolean("options.showLandAlerts", true)) {
            getServer().getPluginManager().registerEvents(pListen, this);
        }

        if (getConfig().getBoolean("SQLite.enable")) {
            db = new SQLiteDatabase(this.getDataFolder() + "/database.db");
            ((SQLiteDatabase)db).setupDatabase();
        }
        else
            db = new MySQLDatabase(getConfig().getString("MySQL.Hostname"), getConfig().getInt("MySQL.Port"), getConfig().getString("MySQL.MySQLDatabase"), getConfig().getString("MySQL.User"), getConfig().getString("MySQL.Password"));


        // Command Executor
        getCommand("landlord").setExecutor(new LandlordCommandExecutor(this));

        //Worldguard Check
        if (!hasWorldGuard() && this.getConfig().getBoolean("worldguard.blockRegionClaim", true)) {
            getLogger().warning("Worldguard not found, worldguard features disabled.");
        } else if (hasWorldGuard()) {
            getLogger().info("Worldguard found!");
            wgHandler = new WorldguardHandler(getWorldGuard());
        }

        //Vault Check
        if (!hasVault() && this.getConfig().getBoolean("economy.enable", true)) {
            getLogger().warning("Vault not found, economy features disabled.");
        } else if (hasVault()) {
            getLogger().info("Vault found!");
            vHandler = new VaultHandler();
            if (!vHandler.hasEconomy()) {
                getLogger().warning("No economy found, economy features disabled.");
            }
        }


        //Register default flags
        if (getConfig().getBoolean("enabled-flags.build")) {
            flagManager.registerFlag(new Build(this));
        }
        if (getConfig().getBoolean("enabled-flags.harmAnimals")) {
            flagManager.registerFlag(new HarmAnimals(this));
        }
        if (getConfig().getBoolean("enabled-flags.useContainers")) {
            flagManager.registerFlag(new UseContainers(this));
        }
        if (getConfig().getBoolean("enabled-flags.tntDamage")) {
            flagManager.registerFlag(new TntDamage(this));
        }
        if (getConfig().getBoolean("enabled-flags.useRedstone")) {
            flagManager.registerFlag(new UseRedstone(this));
        }
        if (getConfig().getBoolean("enabled-flags.openDoor")) {
            flagManager.registerFlag(new OpenDoor(this));
        }
        if (getConfig().getBoolean("enabled-flags.pvp")) {
            flagManager.registerFlag(new PVP(this));
        }
        if (getConfig().getBoolean("enabled-flags.setHome")) {
            flagManager.registerFlag(new SetHome(this));
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " has been disabled!");
        mapManager.removeAllMaps();
        manageViewManager.deactivateAll();
        pListen.clearPtrack();
        db.close();
    }

    @Override
    public FileConfiguration getConfig() {
        return mainConfig.get();
    }

    public FileConfiguration getMessageConfig() {
        return messagesConfig.get();
    }

    private FileConfiguration getMessages() {
        return messagesConfig.get();
    }

    public FlagManager getFlagManager() {
        return flagManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public ViewManager getManageViewManager() {
        return manageViewManager;
    }



    /*
     * ***************************
     *      Dependency Stuff
     * ***************************
     */

    /*
     * **************
     *   Worldguard
     * **************
     */
    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }


    /**
     * Provides access to the Landlord WorldGuardHandler
     *
     * @return ll wg handler
     */
    public WorldguardHandler getWgHandler() {
        return wgHandler;
    }

    public boolean hasWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        //System.out.println("-------- " + plugin.toString());
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin) || !this.getConfig().getBoolean("worldguard.blockRegionClaim", true)) {
            return false;
        }
        /*if(plugin.toString().contains("6.0.0-beta")) {

            getLogger().warning("This WorldGuard version \'6.0.0-beta\' does not work with Landlord, please update it.");
            return false;
        }*/

        return true;
    }

    /*
     * **************
     *     Vault
     * **************
     */

    public boolean hasVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        // WorldGuard may not be loaded
        return !(plugin == null || !this.getConfig().getBoolean("economy.enable", true));
    }

    public VaultHandler getvHandler() {
        return vHandler;
    }


    public AbstractDatabase getDatabase() {
        return db;
    }


    public LandManager getLandManager() {
        return landManager;
    }
}
