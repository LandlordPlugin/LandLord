package biz.princeps.landlord;

import biz.princeps.landlord.api.ICostManager;
import biz.princeps.landlord.api.IDelimitationManager;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IMapManager;
import biz.princeps.landlord.api.IMaterialsManager;
import biz.princeps.landlord.api.IMobManager;
import biz.princeps.landlord.api.IMultiTaskManager;
import biz.princeps.landlord.api.IPlayerManager;
import biz.princeps.landlord.api.IRegenerationManager;
import biz.princeps.landlord.api.IUtilsManager;
import biz.princeps.landlord.api.IVaultManager;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.integrations.LLLuckPerms;
import biz.princeps.landlord.integrations.Towny;
import biz.princeps.landlord.listener.JoinListener;
import biz.princeps.landlord.listener.LandChangeListener;
import biz.princeps.landlord.listener.MapListener;
import biz.princeps.landlord.listener.SecureWorldListener;
import biz.princeps.landlord.manager.DelimitationManager;
import biz.princeps.landlord.manager.LPlayerManager;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.manager.VaultManager;
import biz.princeps.landlord.manager.cost.LandCostManager;
import biz.princeps.landlord.manager.map.MapManager;
import biz.princeps.landlord.multi.MultiTaskManager;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.placeholderapi.LLExpansion;
import biz.princeps.landlord.placeholderapi.LLFeatherBoard;
import biz.princeps.landlord.util.ConfigUtil;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.manager.ConfirmationManager;
import de.eldoria.eldoutilities.bstats.EldoMetrics;
import de.eldoria.eldoutilities.core.EldoUtilities;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Pattern;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class ALandLord extends JavaPlugin implements ILandLord, Listener {

    protected IWorldGuardManager worldGuardManager;
    protected IUtilsManager utilsManager;
    protected IMaterialsManager materialsManager;
    protected IVaultManager vaultManager;
    protected ILangManager langManager;
    protected IPlayerManager lPlayerManager;
    protected IMapManager mapManager;
    protected ICostManager costManager;
    protected IDelimitationManager delimitationManager;
    protected IMobManager mobManager;
    protected IRegenerationManager regenerationManager;
    protected IMultiTaskManager multiTaskManager;

    @Override
    public void onLoad() {
        EldoUtilities.preWarm(this);
    }

    @Override
    public void onEnable() {
        EldoUtilities.ignite(this);

        Options.setConfig(this.getConfig(), getVault() != null);
        setupPrincepsLib();

        checkWorldNames();

        setupConfig();
        setupIntegrations();
        setupItems();
        setupManagers();
        setupListeners();
        setupPlayers();
        setupMultiTaskManager();
        setupMetrics();
        postloadPrincepsLib();
    }

    @Override
    public void onDisable() {
        EldoUtilities.shutdown();

        getLogger().info("Cancelling remaining tasks...");
        int clearedTasks = multiTaskManager.clear();
        getLogger().info(clearedTasks + " tasks have been cancelled!");

        getLogger().info("Clearing all maps...");
        if (mapManager != null) {
            mapManager.removeAllMaps();
        }
        getLogger().info("All maps have been cleared!");

        getLogger().info("Saving player data...");
        if (lPlayerManager != null) {
            getPlayerManager().saveAllOnlineSync();
        }
        getLogger().info("Player data has been saved!");
    }

    /**
     * Checks if shared dependencies (protocollib+vault) are available
     *
     * @return if the dependencies are available
     */
    protected boolean checkDependencies() {
        // shared deps
        if (getVault() == null) {
            getLogger().info("Vault or an economy provider could no be found. Not all features of landlord are working.");
        }
        return true;
    }

    /**
     * Halts the plugin and display a warning message in the log
     *
     * @param warning the warning to be displayed in the log
     */
    protected void haltPlugin(String warning) {
        getLogger().warning(warning);
        this.getPluginLoader().disablePlugin(this);
    }

    /**
     * @return the javaplugin instance
     */
    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    /**
     * In case there was a version change in the config (increment the tiny version variable) this will handle the
     * update by backing up the old config and copying the new config to the right place
     */
    private void setupConfig() {
        this.saveDefaultConfig();
        new ConfigUtil(this).handleConfigUpdate(this.getDataFolder() + "/config.yml", "/config.yml");
        this.saveDefaultConfig();
    }

    /**
     * Sets up princeps lib. PrincepsLib is a dumb library I introduced some time, because I thought I would program
     * more spigot plugins.
     * <p>
     * Here we are :shrug:
     */
    @Override
    public void setupPrincepsLib() {
        PrincepsLib.setPluginInstance(this);
        PrincepsLib.getConfirmationManager().setState(ConfirmationManager.STATE.valueOf(getConfig().getString(
                "ConfirmationDialog.mode")));
        PrincepsLib.getConfirmationManager().setTimout(getConfig().getInt("ConfirmationDialog.timeout"));
    }

    /**
     * Since there is a cyclic dependency on startup I had to pull this one out.
     * Some strings in Princepslib are translatable. set those here.
     */
    @Override
    public void postloadPrincepsLib() {
        PrincepsLib.getTranslateableStrings().setString("Confirmation.accept", langManager.getRawString(
                "Confirmation.accept"));
        PrincepsLib.getTranslateableStrings().setString("Confirmation.decline", langManager.getRawString(
                "Confirmation.decline"));
        PrincepsLib.getTranslateableStrings().setString("noPermissionsCmd", langManager.getRawString(
                "noPermissionsCmd"));

        PrincepsLib.getCommandManager().registerCommand(new Landlordbase(this));
    }

    /**
     * Retrieve the LPlayer objects for all online players (in case of reload) and insert them into the PlayerManager
     */
    private void setupPlayers() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            getPlayerManager().getOffline(onlinePlayer.getUniqueId(), (offline) -> {
                if (offline == null) {
                    this.getPlayerManager().add(new LPlayer(onlinePlayer.getUniqueId()));
                } else {
                    this.getPlayerManager().add(offline);
                }
            });
        }
    }

    /**
     * Sets up a bunch of different manager:
     * a) LangManger: handle the translation files. access messages via this object
     * b) LPlayerManager: handles player related stuff, like claims, last login ...
     * c) OfferManager: handles advertised lands
     * d) MapManager: handles the displaying of the land map
     * e) CostManager: handles the cost calculations
     * f) VaultManager: handles the bridge between landlord and vault
     * g) DelimitationManager: handles the delimitation of single regions
     */
    private void setupManagers() {
        this.langManager = new LangManager(this, getConfig().getString("language", "en"));
        this.lPlayerManager = new LPlayerManager(this);
        this.mapManager = new MapManager(this);
        this.costManager = new LandCostManager(this);
        this.vaultManager = new VaultManager(this, getVault());
        this.delimitationManager = new DelimitationManager(this);
    }

    /**
     * I didnt planned to handle world names containing spaces in the first place, thats why this check exists.
     * Also worldguard doesnt like worldnames with special characters
     */
    private void checkWorldNames() {
        if (!getConfig().getBoolean("DisableStartupWorldWarning")) {
            Pattern pattern = Pattern.compile("[^A-Za-z0-9_-]+");

            for (World world : Bukkit.getWorlds()) {
                if (!pattern.matcher(world.getName()).find()) continue;

                getLogger().warning(
                        "Found an invalid world name (" + world.getName() + ")! LandLord will not work in this " +
                                "world!");
            }
        }
    }

    /**
     * Registers special items (left/right click action; nbt data...) with the princepslib protection
     */
    private void setupItems() {
        //PrincepsLib.getItemManager().registerItem(Maitem.NAME, Maitem.class);
    }

    /**
     * Registers placeholders with different plugins
     * TODO add FeatherBoard nop not gonna happen.
     */
    private void setupIntegrations() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LLExpansion(this).register();
        }
        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            new LLFeatherBoard(this);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            new Towny(this);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            new LLLuckPerms(this);
        }

    }

    /**
     * Registers listeners with spigot.
     * The LandAlerter is a special listener, since it listens on packets.
     */
    private void setupListeners() {
        new JoinListener(this);
        new MapListener(this);
        new LandChangeListener(this);

        if (getConfig().getBoolean("SecureWorld.enable")) {
            new SecureWorldListener(this);
        }
    }

    /**
     * Setup and schedule the MultiTaskManager.
     */
    private void setupMultiTaskManager() {
        this.multiTaskManager = new MultiTaskManager(this);
        multiTaskManager.initTask();
    }

    /**
     * Register bStats metrics https://bstats.org/plugin/bukkit/Landlord
     */
    private void setupMetrics() {
        EldoMetrics metrics = new EldoMetrics(this, 2322);
        if (metrics.isEnabled()) {
            getLogger().info("§2Metrics enabled. Thank you :3");
        }
        //TODO maybe add some interesting statistics
    }

    private Economy getVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        return (rsp == null ? null : rsp.getProvider());
    }

    @Override
    public IWorldGuardManager getWGManager() {
        return worldGuardManager;
    }

    @Override
    public IMaterialsManager getMaterialsManager() {
        return materialsManager;
    }

    @Override
    public IUtilsManager getUtilsManager() {
        return utilsManager;
    }

    @Override
    public IPlayerManager getPlayerManager() {
        return lPlayerManager;
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
        return vaultManager;
    }

    @Override
    public IDelimitationManager getDelimitationManager() {
        return delimitationManager;
    }

    @Override
    public IMobManager getMobManager() {
        return mobManager;
    }

    @Override
    public IRegenerationManager getRegenerationManager() {
        return regenerationManager;
    }

    @Override
    public IMultiTaskManager getMultiTaskManager() {
        return multiTaskManager;
    }

}
