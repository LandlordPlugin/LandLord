package biz.princeps.landlord;

import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.handler.VaultHandler;
import biz.princeps.landlord.handler.WorldGuardHandler;
import biz.princeps.landlord.listener.JoinListener;
import biz.princeps.landlord.listener.LandAlerter;
import biz.princeps.landlord.manager.LPlayerManager;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.manager.map.MapManager;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.persistent.Requests;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.storage.DatabaseAPI;
import biz.princeps.lib.storage.DatabaseType;
import biz.princeps.lib.storage.requests.Conditions;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by spatium on 16.07.17.
 */
public class Landlord extends JavaPlugin {

    private static Landlord instance;
    private static DatabaseAPI databaseAPI;
    private ExecutorService executorService;

    private WorldGuardHandler wgHandler;
    private VaultHandler vaultHandler;

    private LangManager langManager;
    private LPlayerManager lPlayerManager;
    private MapManager mapManager;

    public static Landlord getInstance() {
        return instance;
    }

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
            getLogger().warning("Vault not found! Not all features of landlord are working.");
        } else
            vaultHandler = new VaultHandler(getVault());

        instance = this;

        saveDefaultConfig();
        langManager = new LangManager(this, getConfig().getString("Language", "en"));

        PrincepsLib.setPluginInstance(this);
        databaseAPI = new DatabaseAPI(DatabaseType.valueOf(getConfig().getString("DatabaseType")), getConfig(), new Requests(), "biz.princeps.landlord.persistent");

        manageCommands();
        manageListeners();

        lPlayerManager = new LPlayerManager(databaseAPI);

        mapManager = new MapManager();
        ScoreboardLib.setPluginInstance(this);

        executorService = Executors.newCachedThreadPool();

        //Retrieve the LPlayer objects for all online players (in case of reload)
        Bukkit.getOnlinePlayers().forEach(p -> {
            List<Object> lPlayer = this.getDatabaseAPI().retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("uuid", p.getUniqueId().toString()).create());
            LPlayer lp;
            if (lPlayer.size() > 0)
                lp = (LPlayer) lPlayer.get(0);
            else
                lp = new LPlayer(p.getUniqueId());
            this.getPlayerManager().add(p.getUniqueId(), lp);
        });
    }

    @Override
    public void onDisable() {
        if (mapManager != null)
            mapManager.removeAllMaps();

        Bukkit.getOnlinePlayers().forEach(p -> getPlayerManager().save(p.getUniqueId()));
    }

    private void manageCommands() {
        BukkitCommandManager cmdmanager = new BukkitCommandManager(this);
        cmdmanager.registerCommand(new Landlordbase());
    }

    private void manageListeners() {
        new JoinListener();
        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null)
            new LandAlerter();
        else
            getLogger().warning("ProtocolLib has not been found. This plugin may not function properly!");
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

    public MapManager getMapManager() {
        return mapManager;
    }

    public boolean isVaultEnabled() {
        return getConfig().getBoolean("Economy.enable");
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
