package biz.princeps.landlord;

import biz.princeps.landlord.api.*;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.manager.VaultManager;
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
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Pattern;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class ALandLord extends JavaPlugin implements ILandLord {

    protected IWorldGuardProxy wgproxy;
    protected IUtilsProxy utilsProxy;
    protected IMaterialsProxy materialsProxy;

    protected IVaultManager vaultHandler;
    protected ILangManager langManager;
    protected IPlayerManager lPlayerManager;
    protected IMapManager mapManager;
    protected ICostManager costManager;
    protected IOfferManager offerManager;

    private static TaskChainFactory taskChainFactory;

    private Database db;

    abstract void onPreEnable();

    abstract void onPostEnable();

    @Override
    public void onEnable() {
        if (!checkDependencies()) return;

        onPreEnable();

        Options.setConfig(this.getConfig());
        taskChainFactory = BukkitTaskChainFactory.create(this);
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

        new Updater(this);
        onPostEnable();
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
    }

    protected boolean checkDependencies() {
        // shared deps
        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            haltPlugin("ProtocolLib not found! Please ensure you have the correct version of ProtocolLib in order to use LandLord");
            return false;
        }
        if (getVault() == null) {
            getLogger().warning("Vault not found! Not all features of landlord are working.");
        }
        return true;
    }

    protected void haltPlugin(String warning) {
        getLogger().warning(warning);
        this.getPluginLoader().disablePlugin(this);
    }

    public JavaPlugin getPlugin() {
        return this;
    }

    /**
     * In case there was a version change in the config (increment the tiny version variable) this will handle the
     * update by backing up the old config and copying the new config to the right place
     */
    private void setupConfig() {
        this.saveDefaultConfig();
        ConfigUtil.handleConfigUpdate(this.getDataFolder() + "/config.yml", "/config.yml");
        this.saveDefaultConfig();
    }

    /**
     * Sets up princeps lib. PrincepsLib is a dumb library I introduced some time, because I thought I would program
     * more spigot plugins.
     * <p>
     * Here we are :shrug:
     */
    public void setupPrincepsLib() {
        PrincepsLib.setPluginInstance(this);
        PrincepsLib.getConfirmationManager().setState(ConfirmationManager.STATE.valueOf(getConfig().getString("ConfirmationDialog.mode")));
        PrincepsLib.getConfirmationManager().setTimout(getConfig().getInt("ConfirmationDialog.timeout"));
    }

    /**
     * Since there is a cyclic dependency on startup I had to pull this one out.
     * Some strings in Princepslib are translatable. set those here.
     */
    private void postloadPrincepsLib() {
        PrincepsLib.getTranslateableStrings().setString("Confirmation.accept", langManager.getRawString("Confirmation.accept"));
        PrincepsLib.getTranslateableStrings().setString("Confirmation.decline", langManager.getRawString("Confirmation.decline"));

        PrincepsLib.getCommandManager().registerCommand(new Landlordbase(this));
    }

    /**
     * Sets up the database. H2 and Mysql take the same parameters, so dont wonder about that
     */
    private void setupDatabase() {
        String dbpath = getConfig().getString("MySQL.Database");
        DatabaseType dbtype = DatabaseType.valueOf(getConfig().getString("DatabaseType"));
        if (dbtype == DatabaseType.SQLite) {
            getLogger().warning("SQLite is not longer supported! Use H2 instead!");
            this.getPluginLoader().disablePlugin(this);
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
     */
    private void setupManagers() {
        this.langManager = new LangManager(this, getConfig().getString("language", "en"));

        this.lPlayerManager = new LPlayerManager(db, this);
        this.offerManager = new OfferManager(db);

        this.mapManager = new MapManager(this);
        this.costManager = new CostManager();

        this.vaultHandler = new VaultManager(getVault());
    }

    /**
     * I didnt planned to handle world names containing spaces in the first place, thats why this check exists.
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

    /**
     * Registers listeners with spigot.
     * The LandAlerter is a special listener, since it listens on packets.
     */
    private void setupListeners() {
        new JoinListener();
        new MapManager(this);

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
            Metrics metrics = new Metrics(this);
            //TODO maybe add some interesting statistics
        }
    }


    public Economy getVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        return (rsp == null ? null : rsp.getProvider());
    }

    @Override
    public <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    @Override
    public <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    @Override
    public IWorldGuardProxy getWGProxy() {
        return wgproxy;
    }

    @Override
    public IMaterialsProxy getMatProxy() {
        return materialsProxy;
    }

    @Override
    public IUtilsProxy getUtilsProxy() {
        return utilsProxy;
    }

    @Override
    public IPlayerManager getPlayerManager() {
        return lPlayerManager;
    }

    @Override
    public IOfferManager getOfferManager() {
        return offerManager;
    }

    @Override
    public ICostManager getCostManager() {
        return costManager;
    }

    @Override
    public IMapManager getMapManager() {
        return mapManager;
    }

    @Override
    public ILangManager getLangManager() {
        return langManager;
    }

    @Override
    public IVaultManager getVaultManager() {
        return vaultHandler;
    }
}
