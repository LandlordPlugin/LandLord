package biz.princeps.landlord;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class LandLordLatest extends ALandLord {

    @Override
    public void onEnable() {
        core = new Landlord();
        core.onEnable(this);
        core.setUtilsProxy(new UtilsProxy());
    }

    @Override
    public void onDisable() {
        core.onDisable();
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
        if (getWorldGuard() == null) {
            haltPlugin("WorldGuard not found! Please ensure you have the correct version of WorldGuard in order to use LandLord");
            return false;
        } else {
            String v = getWorldGuard().getDescription().getVersion();
            boolean flag = false;
            if (!v.contains("beta")) {
                try {
                    int version = Integer.valueOf(v.split(";")[1].split("-")[0]);
                    if (version < 1754) {
                        flag = true;
                    }
                } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                    flag = true;
                }
            }
            if (flag) {
                haltPlugin("Invalid WorldGuard Version found. LandLord requires WG 1754+");
                return false;
            }

            String worldeditVerison = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();
            flag = false;
            if (!worldeditVerison.contains("beta")) {
                try {
                    int version = Integer.valueOf(worldeditVerison.split(";")[1].split("-")[0]);
                    if (version < 3937) {
                        flag = true;
                    }
                } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                    flag = true;
                }
            }
            if (flag) {
                haltPlugin("Invalid WorldEdit Version found. LandLord requires WE 3937+");
                return false;
            }

            core.setWgproxy(new WorldGuardProxy(getWorldGuard()));
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
