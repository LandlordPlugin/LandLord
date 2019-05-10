package biz.princeps.landlord;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class LandLord extends ALandLord {

    @Override
    void onPreEnable() {
        this.wgproxy = new WorldGuardProxy(this, getWorldGuard());
        this.utilsProxy = new UtilsProxy();
        this.materialsProxy = new MaterialsProxy();
        this.mobProxy = new MobProxy();

        ((WorldGuardProxy) wgproxy).initCache();
    }

    @Override
    void onPostEnable() {

    }

    @Override
    public void onEnable() {
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
     * b) protocollib
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
        if (!super.checkDependencies()) return false;

        // Dependency stuff
        if (!Bukkit.getVersion().contains("1.12.2")) {
            haltPlugin("Invalid spigot version detected! LandLord requires 1.12.2");
            return false;
        }

        if (getWorldGuard() == null) {
            haltPlugin("WorldGuard not found! Please ensure you have the correct version of WorldGuard in order to use LandLord");
            return false;
        } else {
            String v = Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
            if (!v.contains("6.2.2")) {
                haltPlugin("Invalid WorldGuard Version found. LandLord requires WG 6.2.2!");
                return false;
            }

            String worldeditVerison = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();
            if (!worldeditVerison.contains("6.1.9")) {
                haltPlugin("Invalid WorldEdit Version found. LandLord requires WE 6.1.9");
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