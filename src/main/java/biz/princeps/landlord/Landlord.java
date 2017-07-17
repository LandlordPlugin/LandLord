package biz.princeps.landlord;

import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.handler.VaultHandler;
import biz.princeps.landlord.handler.WorldGuardHandler;
import biz.princeps.landlord.listener.JoinListener;
import biz.princeps.landlord.manager.LPlayerManager;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.persistent.Requests;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.storage.DatabaseAPI;
import biz.princeps.lib.storage.DatabaseType;
import co.aikar.commands.BukkitCommandManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by spatium on 16.07.17.
 */
public class Landlord extends JavaPlugin {

    private static Landlord instance;
    private static DatabaseAPI databaseAPI;

    private WorldGuardHandler wgHandler;
    private VaultHandler vaultHandler;

    private LangManager langManager;
    private LPlayerManager lPlayerManager;

    @Override
    public void onEnable() {
        // Dependency stuff
        if (getWorldGuard() == null) {
            getLogger().warning("WorldGuard not found! Please ensure you have the correct version of WorldGuard in order to use LandLord");
            getPluginLoader().disablePlugin(this);
            return;
        } else
            wgHandler = new WorldGuardHandler(getWorldGuard());

        if (getVault() == null) {
            getLogger().warning("Vault not found! Please ensure you have the correct version of Vault in order to use LandLord");
            getPluginLoader().disablePlugin(this);
            return;
        } else
            vaultHandler = new VaultHandler(getVault());

        instance = this;

        saveDefaultConfig();
        langManager = new LangManager(this, getConfig().getString("Language", "en"));
        lPlayerManager = new LPlayerManager();

        manageCommands();
        manageListners();
        PrincepsLib.setPluginInstance(this);
        databaseAPI = new DatabaseAPI(DatabaseType.valueOf(getConfig().getString("DatabaseType")), getConfig(), new Requests(), "biz.princeps.landlord.persistent");
    }


    @Override
    public void onDisable() {

    }


    private void manageCommands() {
        BukkitCommandManager cmdmanager = new BukkitCommandManager(this);
        cmdmanager.registerCommand(new Landlordbase());
    }

    private void manageListners() {
        new JoinListener();
    }


    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    private Economy getVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        return (rsp == null ? null : rsp.getProvider());
    }

    public static Landlord getInstance() {
        return instance;
    }

    public DatabaseAPI getDatabaseAPI() {
        return databaseAPI;
    }

    public WorldGuardHandler getWgHandler() {
        return wgHandler;
    }

    public VaultHandler getVaultHandler() {
        return vaultHandler;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public LPlayerManager getPlayerManager() {
        return lPlayerManager;
    }
}
