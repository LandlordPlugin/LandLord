package biz.princeps.landlord;

import biz.princeps.landlord.listener.PistonOverwriter;
import biz.princeps.landlord.listener.WGRegenListener;
import biz.princeps.landlord.manager.MaterialsManager;
import biz.princeps.landlord.manager.MobsManager;
import biz.princeps.landlord.manager.UtilsManager;
import biz.princeps.landlord.manager.WorldGuardManager;
import biz.princeps.landlord.regenerators.RegenerationManager;
import biz.princeps.landlord.regenerators.WGRegenerator;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.FarewellFlag;
import com.sk89q.worldguard.session.handler.GreetingFlag;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class LandLord extends ALandLord {

    @Override
    public void onLoad() {
        WorldGuardManager.initFlags();
        super.onLoad();
    }

    @Override
    public void onEnable() {
        if (!checkDependencies()) {
            return;
        }

        this.worldGuardManager = new WorldGuardManager(this, getWorldGuard());
        this.utilsManager = new UtilsManager();
        this.materialsManager = new MaterialsManager(this);
        this.mobManager = new MobsManager(this);

        if (getConfig().getString("Regeneration.provider", "default").equalsIgnoreCase("wg")) {
            File folder = new File(getDataFolder(), "chunksaves");
            folder.mkdir();
            this.regenerationManager = new WGRegenerator(this);
            new WGRegenListener(this);
        } else {
            this.regenerationManager = new RegenerationManager();
        }

        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(new LandSessionHandler.Factory(this, (WorldGuardManager) worldGuardManager), null);
        sessionManager.unregisterHandler(GreetingFlag.FACTORY);
        sessionManager.unregisterHandler(FarewellFlag.FACTORY);
        ((WorldGuardManager) worldGuardManager).initCache();

        super.onEnable();

        new PistonOverwriter(this);

        // Handle 1.18 auto-migration.
        // https://minecraft.fandom.com/wiki/Data_version
        int currentDataVersion = getServer().getUnsafe().getDataVersion();
        if (currentDataVersion > 2825 && getConfig().getInt("ClaimHeight.overworld-topY") <= 255) {
            getLogger().warning("It appears that you are running Minecraft 1.18 for the first time. " +
                    "Due to world height changes in overworld, Landlord will try to convert heights in configuration and on lands. " +
                    "This operation could be unsuccessful, so don't hesitate to check configuration/regions, contact us and use the command '/ll update -c'!");
            getServer().dispatchCommand(getServer().getConsoleSender(), "ll update -c");
        }
    }


    @Override
    public void onDisable() {
        super.onDisable();
    }

    /**
     * Checks versions+availability for
     * a) spigot
     * c) worldguard
     * d) worldedit
     * e) vault
     * <p>
     * Historically during the 1.13.2 development there was a lot of chanage in worldguard/edit. People constantly
     * complained about stuff not working because of some dumb updates that require variable renaming.
     * <p>
     * These checks should not be here in the first place in my opinion. So to my future me/anybody else: might wanna
     * get rid of this!!
     *
     * @return returns if all dependencies are satisfied
     */
    @Override
    protected boolean checkDependencies() {
        if (!super.checkDependencies())
            return false;

        // Dependency stuff
        String version = getServer().getVersion();
        if (!version.contains("1.13.2") && !version.contains("1.14") && !version.contains("1.15") && !version.contains("1.16") && !version.contains("1.17")) {
            haltPlugin("Invalid Spigot version detected! LandLord latest requires 1.13.2/1.14.x/1.15.x/1.16.x/1.17.x, use Legacy version for 1.12.2!");
            return false;
        }

        if (getWorldGuard() == null) {
            haltPlugin("WorldGuard not found! Please ensure you have the correct version of WorldGuard in order to " +
                    "use LandLord. Maybe adequate WorldEdit plugin missing?");
            return false;
        } else {
            String worldGuardVersion = getWorldGuard().getDescription().getVersion();
            if (worldGuardVersion.charAt(0) != '7') {
                haltPlugin("Invalid WorldGuard Version found. LandLord requires WG 7.0.0+ ! You have WG " + worldGuardVersion);
                return false;
            }
        }
        return true;
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }
}
