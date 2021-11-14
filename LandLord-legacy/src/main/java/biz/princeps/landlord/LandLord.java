package biz.princeps.landlord;

import biz.princeps.landlord.listener.PistonOverwriter;
import biz.princeps.landlord.manager.MaterialsManager;
import biz.princeps.landlord.manager.MobsManager;
import biz.princeps.landlord.manager.UtilsManager;
import biz.princeps.landlord.manager.WorldGuardManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.FarewellFlag;
import com.sk89q.worldguard.session.handler.GreetingFlag;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class LandLord extends ALandLord implements Listener {

    @Override
    public void onLoad() {
        if (getWorldGuard() != null) {
            WorldGuardManager.initFlags(getWorldGuard());
        }
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
            // there is only the default method available for 1.12.2
        }
        this.regenerationManager = World::regenerateChunk;

        SessionManager sessionManager = WorldGuardPlugin.inst().getSessionManager();
        sessionManager.registerHandler(new LandSessionHandler.Factory(this, (WorldGuardManager) worldGuardManager), null);
        sessionManager.unregisterHandler(GreetingFlag.FACTORY);
        sessionManager.unregisterHandler(FarewellFlag.FACTORY);

        ((WorldGuardManager) worldGuardManager).initCache();

        super.onEnable();

        new PistonOverwriter(this);
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
        if (!getServer().getVersion().contains("1.12.2")) {
            haltPlugin("Invalid Spigot version detected! LandLord requires 1.12.2, use Latest version for 1.13.2+!");
            return false;
        }

        if (getWorldGuard() == null) {
            haltPlugin("WorldGuard not found! Please ensure you have the correct version of WorldGuard in order to " +
                    "use LandLord. Maybe adequate WorldEdit plugin missing?");
            return false;
        } else {
            String worldGuardVersion = getWorldGuard().getDescription().getVersion();
            if (!worldGuardVersion.contains("6.2.2")) {
                haltPlugin("Invalid WorldGuard Version found. LandLord requires WG 6.2.2! : You have WG " + worldGuardVersion);
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
