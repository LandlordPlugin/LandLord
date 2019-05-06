package biz.princeps.landlord;

import biz.princeps.landlord.api.IUtilsProxy;
import biz.princeps.landlord.api.IWorldGuardProxy;
import biz.princeps.landlord.api.LandLordAPI;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.handler.VaultHandler;
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
import biz.princeps.landlord.util.Updater;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.manager.ConfirmationManager;
import biz.princeps.lib.storage_old.DatabaseType;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/7/17
 */
public class Landlord implements LandLordAPI {

    private static Landlord instance;
    private static TaskChainFactory taskChainFactory;
    private ALandLord pluginInstance;

    private IWorldGuardProxy wgproxy;
    private IUtilsProxy utilsProxy;

    private Database db;
    private ExecutorService executorService;
    private VaultHandler vaultHandler;

    private LangManager langManager;
    private LPlayerManager lPlayerManager;
    private MapManager mapManager;
    private CostManager costManager;
    private OfferManager offerManager;

    public static Landlord getInstance() {
        return instance;
    }

    public ALandLord getPluginInstance() {
        return this.pluginInstance;
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

    public void onEnable(ALandLord landLord) {
        pluginInstance = landLord;
        if (!pluginInstance.checkDependencies()) return;

        taskChainFactory = BukkitTaskChainFactory.create(landLord);
        instance = this;
        setupPrincepsLib();

        checkWorldNames();

        setupConfig();
        setupDatabase();
        setupListeners();
        setupPlacerholders();
        setupItems();
        setupManagers();
        setupPlayers();
        setupMetrics();
        postloadPrincepsLib();

        new Updater();
    }

    /**
     * In case there was a version change in the config (increment the tiny version variable) this will handle the
     * update by backing up the old config and copying the new config to the right place
     */
    private void setupConfig() {
        pluginInstance.saveDefaultConfig();
        ConfigUtil.handleConfigUpdate(this.getDataFolder() + "/config.yml", "/config.yml");
        pluginInstance.saveDefaultConfig();
    }

    /**
     * Sets up princeps lib. PrincepsLib is a dumb library I introduced some time, because I thought I would program
     * more spigot plugins.
     * <p>
     * Here we are :shrug:
     */
    public void setupPrincepsLib() {
        PrincepsLib.setPluginInstance(pluginInstance);
        PrincepsLib.getConfirmationManager().setState(ConfirmationManager.STATE.valueOf(getConfig().getString("ConfirmationDialog.mode")));
        PrincepsLib.getConfirmationManager().setTimout(getConfig().getInt("ConfirmationDialog.timeout"));
    }

    /**
     * Since there is a cyclic dependency on startup I had to pull this one out.
     * Some strings in Princepslib are translatable. set those here.
     */
    private void postloadPrincepsLib() {
        PrincepsLib.getTranslateableStrings().setString("Confirmation.accept", langManager.getString("Confirmation.accept"));
        PrincepsLib.getTranslateableStrings().setString("Confirmation.decline", langManager.getString("Confirmation.decline"));

        PrincepsLib.getCommandManager().registerCommand(new Landlordbase());
    }

    /**
     * Sets up the database. H2 and Mysql take the same parameters, so dont wonder about that
     */
    private void setupDatabase() {
        String dbpath = getConfig().getString("MySQL.Database");
        DatabaseType dbtype = DatabaseType.valueOf(getConfig().getString("DatabaseType"));
        if (dbtype == DatabaseType.SQLite) {
            getLogger().warning("SQLite is not longer supported! Use H2 instead!");
            pluginInstance.getPluginLoader().disablePlugin(pluginInstance);
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

    /**
     * Retrieve the LPlayer objects for all online players (in case of reload)
     */
    private void setupPlayers() {
        Bukkit.getOnlinePlayers().forEach(p -> lPlayerManager.getOfflinePlayerAsync(p.getUniqueId(), lPlayer1 -> {
            if (lPlayer1 == null) {
                this.getPlayerManager().add(new LPlayer(p.getUniqueId()));
            } else {
                this.getPlayerManager().add((LPlayer) lPlayer1);
            }
        }));
    }

    /**
     * Sets up a bunch of different manager:
     * a) LangManger: handle the translation files. access messages via this object
     * b) LPlayerManager: handles player related stuff, like claims, last login ...
     * c) OfferManager: handles advertised lands
     * d) MapManager: handles the displaying of the land map
     * e) CostManager: handles the cost calculations
     * f) ThreadPool: For some parallel methods
     * TODO recheck threadpool and maybe delete it in favour of BukkitRunnables
     */
    private void setupManagers() {
        langManager = new LangManager(this, getConfig().getString("language", "en"));

        lPlayerManager = new LPlayerManager(db);
        offerManager = new OfferManager(db);

        mapManager = new MapManager();
        costManager = new CostManager();

        executorService = Executors.newCachedThreadPool();
    }

    /**
     * I didnt planned to handle world names containing spaces in the first place, thats why this check exists.
     * TODO add check for special signs in the world name
     */
    private void checkWorldNames() {
        if (!getConfig().getBoolean("DisableStartupWorldWarning")) {
            Bukkit.getWorlds().stream().filter(w -> Pattern.compile("[^A-Za-z0-9_-]+").matcher(w.getName()).find()).forEach(w -> getLogger().warning("Found an invalid world name (" + w.getName() + ")! LandLord will not work in this world!"));
        }
    }

    /**
     * Registers special items (left/right click action; nbt data...) with the princepslib handler
     */
    private void setupItems() {
        PrincepsLib.getItemManager().registerItem(Maitem.NAME, Maitem.class);
    }

    /**
     * Registers placeholders with different plugins
     * TODO add FeatherBoard
     */
    private void setupPlacerholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LandLordPlacehodlers(this).hook();
        }
    }

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

    /**
     * Registers listeners with spigot.
     * The LandAlerter is a special listener, since it listens on packets.
     */
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

    /**
     * Register bStats metrics https://bstats.org/plugin/bukkit/Landlord
     */
    private void setupMetrics() {
        if (getConfig().getBoolean("EnableMetrics")) {
            Metrics metrics = new Metrics(pluginInstance);
            //TODO maybe add some interesting statistics
        }
    }



    public File getDataFolder() {
        return pluginInstance.getDataFolder();
    }

    public FileConfiguration getConfig() {
        return pluginInstance.getConfig();
    }

    public Server getServer() {
        return pluginInstance.getServer();
    }

    public Logger getLogger() {
        return pluginInstance.getLogger();
    }

    public IWorldGuardProxy getWgproxy() {
        return wgproxy;
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

    public IUtilsProxy getUtilsProxy() {
        return utilsProxy;
    }

    public void setVaultHandler(VaultHandler vaultHandler) {
        if (this.vaultHandler == null) {
            this.vaultHandler = vaultHandler;
            // so that it cannot be modified
        }
    }

    public void setWgproxy(IWorldGuardProxy wgproxy) {
        if (this.vaultHandler == null) {
            this.wgproxy = wgproxy;
        }
    }

    public void setUtilsProxy(IUtilsProxy utilsProxy) {
        this.utilsProxy = utilsProxy;
    }
}
