package biz.princeps.landlord;

import biz.princeps.landlord.api.LandLordAPI;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.handler.VaultHandler;
import biz.princeps.landlord.handler.WorldGuardHandler;
import biz.princeps.landlord.items.Maitem;
import biz.princeps.landlord.listener.JoinListener;
import biz.princeps.landlord.listener.LandAlerter;
import biz.princeps.landlord.listener.SecureWorldListener;
import biz.princeps.landlord.manager.CostManager;
import biz.princeps.landlord.manager.LPlayerManager;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.manager.OfferManager;
import biz.princeps.landlord.manager.map.MapManager;
import biz.princeps.landlord.persistent.Database;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.placeholderapi.LandLordPlacehodlers;
import biz.princeps.landlord.util.ConfigUtil;
import biz.princeps.landlord.util.Metrics;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.landlord.util.Updater;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.manager.ConfirmationManager;
import biz.princeps.lib.storage_old.DatabaseType;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/7/17
 */
public class Landlord extends JavaPlugin implements LandLordAPI {

    private static Landlord instance;
    private static TaskChainFactory taskChainFactory;
    private Database db;
    private ExecutorService executorService;
    private WorldGuardHandler wgHandler;
    private VaultHandler vaultHandler;

    private LangManager langManager;
    private LPlayerManager lPlayerManager;
    private MapManager mapManager;
    private CostManager costManager;
    private OfferManager offerManager;

    public static Landlord getInstance() {
        return instance;
    }

    /**
     * Task Chain Stuff
     **/
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    @Override
    public void onEnable() {
        boolean everythingFine = checkVersions();
        if (!everythingFine) {
            return;
        }

        instance = this;
        setupPrincepsLib();

        checkWorldNames();

        setupConfig();
        setupDatabase();
        setupListeners();
        setupPlacerholders();
        setupItems();
        setupManagers();
        setupCommands();
        setupPlayers();
        setupMetrics();
        setupTranslateableStrings();

        new Updater();
    }

    private boolean checkVersions() {
        if (!Bukkit.getVersion().contains("1.13.2")) {
            haltPlugin("Invalid spigot version detected! LandLord requires 1.13.2");
            return false;
        }

        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            haltPlugin("ProtocolLib not found! Please ensure you have the correct version of ProtocolLib in order to use LandLord");
            return false;
        }

        // Dependency stuff
        if (getWorldGuard() == null) {
            haltPlugin("WorldGuard not found! Please ensure you have the correct version of WorldGuard in order to use LandLord");
            return false;
        } else {
            String v = Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
            boolean flag = false;
            try {
                int version = Integer.valueOf(v.split(";")[1].split("-")[0]);
                if (version < 1754) {
                    flag = true;
                }
            } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                flag = true;
            }
            if (flag) {
                haltPlugin("Invalid WorldGuard Version found. LandLord requires WG 1754+");
                return false;
            }

            String worldeditVerison = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();
            flag = false;
            try {
                int version = Integer.valueOf(worldeditVerison.split(";")[1].split("-")[0]);
                if (version < 3937) {
                    flag = true;
                }
            } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                flag = true;
            }
            if (flag) {
                haltPlugin("Invalid WorldEdit Version found. LandLord requires WE 3937+");
                return false;
            }

            wgHandler = new WorldGuardHandler(getWorldGuard());
        }

        if (getVault() == null) {
            getLogger().warning("Vault not found! Not all features of landlord are working.");
        } else {
            vaultHandler = new VaultHandler(getVault());
        }

        return true;
    }


    private void setupConfig() {
        saveDefaultConfig();
        ConfigUtil.handleConfigUpdate(this.getDataFolder() + "/config.yml", "/config.yml");
        saveDefaultConfig();
    }

    private void setupPrincepsLib() {
        PrincepsLib.setPluginInstance(this);
        PrincepsLib.getConfirmationManager().setState(ConfirmationManager.STATE.valueOf(getConfig().getString("ConfirmationDialog.mode")));
        PrincepsLib.getConfirmationManager().setTimout(getConfig().getInt("ConfirmationDialog.timeout"));
        taskChainFactory = BukkitTaskChainFactory.create(this);
    }

    private void setupDatabase() {
        String dbpath = getConfig().getString("MySQL.Database");
        DatabaseType dbtype = DatabaseType.valueOf(getConfig().getString("DatabaseType"));
        if (dbtype == DatabaseType.SQLite) {
            getLogger().warning("SQLite is not longer supported! Use H2 instead!");
            getPluginLoader().disablePlugin(this);
        } else if (dbtype == DatabaseType.H2) {
            dbpath = getDataFolder() + "/" + getConfig().getString("MySQL.Database");
        }
        db = new Database(getLogger(), DatabaseType.valueOf(getConfig().getString("DatabaseType")),
                getConfig().getString("MySQL.Hostname"),
                getConfig().getString("MySQL.Port"),
                getConfig().getString("MySQL.User"),
                getConfig().getString("MySQL.Password"),
                dbpath);
    }

    private void setupPlayers() {
        //Retrieve the LPlayer objects for all online players (in case of reload)
        Bukkit.getOnlinePlayers().forEach(p -> {
            lPlayerManager.getOfflinePlayerAsync(p.getUniqueId(), lPlayer1 -> {
                if (lPlayer1 == null) {
                    this.getPlayerManager().add(new LPlayer(p.getUniqueId()));
                } else {
                    this.getPlayerManager().add(lPlayer1);
                }
            });
        });
    }

    private void setupManagers() {
        langManager = new LangManager(this, getConfig().getString("language", "en"));

        lPlayerManager = new LPlayerManager(db);
        offerManager = new OfferManager(db);

        mapManager = new MapManager();
        costManager = new CostManager();

        executorService = Executors.newCachedThreadPool();
    }

    private void haltPlugin(String warning) {
        getLogger().warning(warning);
        getPluginLoader().disablePlugin(this);
    }

    private void setupTranslateableStrings() {
        PrincepsLib.getTranslateableStrings().setString("Confirmation.accept", langManager.getString("Confirmation.accept"));
        PrincepsLib.getTranslateableStrings().setString("Confirmation.decline", langManager.getString("Confirmation.decline"));
    }

    private void checkWorldNames() {
        if (getConfig().getBoolean("DisableStartupWorldWarning")) {
            Bukkit.getWorlds().stream().filter(w -> w.getName().contains(" ")).forEach(w -> getLogger().warning("Found an invalid world name (" + w.getName() + ")! LandLord will not work in this world!"));
            getLogger().warning("Your world name may not contain special signs and must consist out of one word");
        }
    }

    private void setupItems() {
        PrincepsLib.getItemManager().registerItem(Maitem.NAME, Maitem.class);
    }

    private void setupPlacerholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LandLordPlacehodlers(this).hook();
        }
    }

    @Override
    public void onDisable() {
        if (mapManager != null) {
            mapManager.removeAllMaps();
        }
        Bukkit.getOnlinePlayers().forEach(p -> getPlayerManager().saveSync(getPlayerManager().get(p.getUniqueId())));
        if (db != null) {
            db.close();
        }
        instance = null;
    }

    public void setupCommands() {
        Landlordbase landlordbase = new Landlordbase();
        PrincepsLib.getCommandManager().registerCommand(landlordbase);
    }

    private void setupListeners() {
        new JoinListener();
        new MapManager();

        if (getConfig().getBoolean("SecureWorld.enable")) {
            new SecureWorldListener();
        }
        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            new LandAlerter();
        } else {
            getLogger().warning("ProtocolLib has not been found. LandAlerts wont function properly");
        }
    }

    private void setupMetrics() {
        if (getConfig().getBoolean("EnableMetrics")) {
            Metrics metrics = new Metrics(this);
            //TODO maybe add some interesting statistics
        }
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public Economy getVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        return (rsp == null ? null : rsp.getProvider());
    }

    public Database getDB() {
        return db;
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

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public CostManager getCostManager() {
        return costManager;
    }

    public OfferManager getOfferManager() {
        return offerManager;
    }

    /**
     * API Methods
     **/
    @Override
    public OwnedLand getLand(Location loc) {
        return wgHandler.getRegion(loc);
    }

    @Override
    public List<ProtectedRegion> getRegions(UUID id, World world) {
        return wgHandler.getRegions(id, world);
    }

    @Override
    public OwnedLand getLand(Chunk chunk) {
        return wgHandler.getRegion(chunk);
    }

    @Override
    public OwnedLand getLand(ProtectedRegion protectedRegion) {
        return wgHandler.getRegion(protectedRegion);
    }

    @Override
    public boolean isLLRegion(String a) {

        String[] split = a.split("_");
        try {
            int x = Integer.valueOf(split[1]);
            int z = Integer.valueOf(split[2]);
            Bukkit.getWorld(split[0]).getChunkAt(x, z);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
