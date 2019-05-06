package biz.princeps.landlord.handler;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class WorldGuardHandler {

    private WorldGuardPlugin wgPlugin;
    private WorldGuard wg;

    public WorldGuardHandler(WorldGuardPlugin wgPlugin) {
        this.wg = WorldGuard.getInstance();
        this.wgPlugin = wgPlugin;
    }





    public RegionContainer getRegionContainer() {
        return wg.getPlatform().getRegionContainer();
    }

    public OwnedLand getRegion(ProtectedRegion pr) {
        if (pr == null) return null;

        return new OwnedLand(pr);
    }

    public Collection<ProtectedRegion> getRegions(World w) {
        com.sk89q.worldedit.world.World worldByName = getWg().getPlatform().getMatcher().getWorldByName(w.getName());
        return Objects.requireNonNull(getRegionContainer().get(worldByName)).getRegions().values();
    }

    public OwnedLand getRegion(String name) {
        return getRegion(getRegionAsPr(name));
    }

    /**
     * Convert a region name to a protectedregion object
     *
     * @param name the name you want the pr for
     * @return the corresponding pr if available else null
     */
    public ProtectedRegion getRegionAsPr(String name) {
        if (!isLLRegion(name)) {
            return null;
        }
        com.sk89q.worldedit.world.World w = getWg().getPlatform().getMatcher().getWorldByName(getWorld(name).getName());
        return Objects.requireNonNull(getRegionContainer().get(w)).getRegion(name);
    }

    public World getWorld(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return null;
        }

        // Handle world names with multiple spaces in the name
        StringBuilder sb = new StringBuilder(splitted[0]);
        for (int i = 1; i < splitted.length - 2; i++) {
            sb.append("_").append(splitted[i]);
        }

        return Bukkit.getWorld(sb.toString());
    }

    /**
     * Returns the x coordinate of a landname
     */
    public int getX(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return Integer.MIN_VALUE;
        }

        try {
            return Integer.parseInt(splitted[splitted.length - 2]);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Returns the z coordinate of a landname
     */
    public int getZ(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return Integer.MIN_VALUE;
        }

        try {
            return Integer.parseInt(splitted[splitted.length - 1]);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Checks if a string is a valid ll region e.g. world_12_34
     */
    public boolean isLLRegion(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return false;
        }

        StringBuilder sb = new StringBuilder(splitted[0]);
        for (int i = 1; i < splitted.length - 2; i++) {
            sb.append("_").append(splitted[i]);
        }

        World world = Bukkit.getWorld(sb.toString());
        if (world == null)
            return false;

        try {
            Integer.parseInt(splitted[splitted.length - 2]);
            Integer.parseInt(splitted[splitted.length - 1]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void unclaim(World world, String regionname) {
        getRegionManager(world).removeRegion(regionname);
    }

    public List<Flag<?>> getFlags() {
        return wg.getFlagRegistry().getAll();
    }

    public ProtectedCuboidRegion setDefaultFlags(ProtectedCuboidRegion region) {
        if (region == null) return null;

        OwnedLand land = new OwnedLand(region);
        region.setFlag(Flags.FAREWELL_MESSAGE, Landlord.getInstance().getLangManager().getRawString("Alerts.defaultFarewell")
                .replace("%owner%", land.printOwners()));

        region.setFlag(Flags.GREET_MESSAGE, Landlord.getInstance().getLangManager().getRawString("Alerts.defaultGreeting")
                .replace("%owner%", land.printOwners()));

        List<String> flaggy = Landlord.getInstance().getConfig().getStringList("Flags");
        Set<String> flags = new HashSet<>();

        flaggy.forEach(s -> flags.add(s.split(" ")[0]));

        //Iterate over all existing flags
        for (Flag<?> flag : wg.getFlagRegistry().getAll()) {
            if (flag instanceof StateFlag) {
                boolean failed = false;
                if (flags.contains(flag.getName())) {
                    // Filters the config list for the right line and split that line in the mid at :
                    String[] rules = flaggy.stream().filter(s -> s.startsWith(flag.getName())).findFirst().get().split(":");
                    if (rules.length == 2) {

                        String[] defSplit = rules[0].split(" ");
                        if (defSplit.length == 3) {
                            StateFlag.State state = StateFlag.State.valueOf(defSplit[1].toUpperCase());
                            if (defSplit[2].equals("nonmembers"))
                                region.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);

                            region.setFlag((StateFlag) flag, state);
                        } else {
                            failed = true;
                        }
                    } else {
                        failed = true;
                    }
                }

                if (failed) {
                    Bukkit.getLogger().warning("ERROR: Your flag definition is invalid!");
                    break;
                }
            }
        }
        return region;
    }

    public WorldGuardPlugin getWGPlugin() {
        return wgPlugin;
    }

    public WorldGuard getWg() {
        return wg;
    }

    public Map<Chunk, OwnedLand> getNearbyLands(Location loc, int offsetX, int offsetZ) {

        Map<Chunk, OwnedLand> lands = new HashMap<>();
        int xCoord = loc.getChunk().getX();
        int zCoord = loc.getChunk().getZ();
        for (int x = xCoord - offsetX; x <= xCoord + offsetX; x++) {
            for (int z = zCoord - offsetZ; z <= zCoord + offsetZ; z++) {
                Chunk chunk = loc.getWorld().getChunkAt(x, z);
                lands.put(chunk, getRegion(chunk));
            }
        }
        return lands;
    }





}
