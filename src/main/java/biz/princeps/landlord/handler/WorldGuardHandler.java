package biz.princeps.landlord.handler;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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

    public WorldGuardHandler(WorldGuardPlugin wgPlugin) {
        this.wgPlugin = wgPlugin;
    }

    public void claim(Chunk chunk, UUID owner) {
        Location down = chunk.getBlock(0, 0, 0).getLocation();
        Location upper = chunk.getBlock(15, 255, 15).getLocation();

        this.claim(owner, OwnedLand.getName(chunk), chunk.getWorld(), down, upper);
    }

    public void claim(UUID owner, String landname, World world, Location down, Location upper) {
        BlockVector vec1 = OwnedLand.locationToVec(down);
        BlockVector vec2 = OwnedLand.locationToVec(upper);

        ProtectedCuboidRegion pr = new ProtectedCuboidRegion(landname, vec1, vec2);

        DefaultDomain ownerDomain = new DefaultDomain();
        ownerDomain.addPlayer(owner);
        pr.setOwners(ownerDomain);

        // flag management
        pr = setDefaultFlags(pr, down.getChunk());
        RegionManager manager = getRegionManager(world);

        if (manager != null) {
            manager.addRegion(pr);
        }
    }

    public OwnedLand getRegion(Chunk chunk) {
        RegionManager manager = getRegionManager(chunk.getWorld());
        ProtectedRegion pr = manager != null ? manager.getRegion(OwnedLand.getName(chunk)) : null;
        return (pr != null ? new OwnedLand(pr, chunk) : null);
    }

    public OwnedLand getRegion(Location loc) {
        return getRegion(loc.getChunk());
    }

    public RegionContainer getRegionContainer() {
        return wgPlugin.getRegionContainer();
    }

    public OwnedLand getRegion(ProtectedRegion pr) {
        return getRegion(pr.getId());
    }

    public OwnedLand getRegion(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return null;
        }

        StringBuilder sb = new StringBuilder(splitted[0]);
        for (int i = 1; i < splitted.length - 2; i++) {
            sb.append("_").append(splitted[i]);
        }

        World world = Bukkit.getWorld(sb.toString());
        if (world == null)
            return null;

        try {
            int x = Integer.parseInt(splitted[splitted.length - 2]);
            int z = Integer.parseInt(splitted[splitted.length - 1]);
            if (!world.isChunkLoaded(x, z)) {
                world.loadChunk(x, z);
            }
            Chunk chunk = world.getChunkAt(x, z);
            return getRegion(chunk);
        } catch (NumberFormatException e) {
            return null;
        }
    }

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

    public FlagRegistry getFlags() {
        return wgPlugin.getFlagRegistry();
    }

    public ProtectedCuboidRegion setDefaultFlags(ProtectedCuboidRegion region, Chunk chunk) {
        OwnedLand land = new OwnedLand(region, chunk);
        region.setFlag(DefaultFlag.FAREWELL_MESSAGE, Landlord.getInstance().getLangManager().getRawString("Alerts.defaultFarewell")
                .replace("%owner%", land.printOwners()));

        region.setFlag(DefaultFlag.GREET_MESSAGE, Landlord.getInstance().getLangManager().getRawString("Alerts.defaultGreeting")
                .replace("%owner%", land.printOwners()));

        List<String> flaggy = Landlord.getInstance().getConfig().getStringList("Flags");
        Set<String> flags = new HashSet<>();

        flaggy.forEach(s -> flags.add(s.split(" ")[0]));

        //Iterate over all existing flags
        for (Flag<?> flag : wgPlugin.getFlagRegistry()) {
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

    public List<ProtectedRegion> getRegions(UUID id, World world) {
        List<ProtectedRegion> regions = new ArrayList<>();
        for (ProtectedRegion protectedRegion : getRegionManager(world).getRegions().values()) {
            if (protectedRegion.getOwners().getUniqueIds().contains(id)) {
                if (getRegion(protectedRegion) != null) {
                    regions.add(protectedRegion);
                }
            }
        }
        return regions;
    }

    public RegionManager getRegionManager(World world) {
        RegionContainer regionContainer = getRegionContainer();
        return regionContainer.get(world);
    }

    public boolean canClaim(Player player, Chunk currChunk) {
        RegionManager regionManager = getRegionManager(player.getWorld());
        if (regionManager != null) {
            Vector v1 = currChunk.getBlock(0, 0, 0).getLocation().toVector();
            Vector v2 = currChunk.getBlock(15, 127, 15).getLocation().toVector();

            ProtectedRegion check = new ProtectedCuboidRegion("check",
                    new BlockVector(v1.getX(), v1.getY(), v1.getZ()),
                    new BlockVector(v2.getX(), v2.getY(), v2.getZ()));
            List<ProtectedRegion> intersects = check
                    .getIntersectingRegions(new ArrayList<>(regionManager.getRegions().values()));
            for (ProtectedRegion intersect : intersects) {
                // check this out, might not work. canBuild was removed in 1.13.1 and Im not sure if isMemberOfAll is equivalent
                // 10/26/18 looks like its working:
                if (!regionManager.getApplicableRegions(intersect).isMemberOfAll(wgPlugin.wrapPlayer(player))) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getRegionCountOfPlayer(UUID id) {
        int count = 0;
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op != null) {
            List<String> worlds = Landlord.getInstance().getConfig().getStringList("disabled-worlds");

            for (World world : Bukkit.getWorlds()) {
                // Only count enabled worlds
                if (!worlds.contains(world.getName())) {
                    RegionManager rm = getRegionManager(world);
                    LocalPlayer localPlayer = getWGPlugin().wrapOfflinePlayer(op);
                    count += rm.getRegionCountOfPlayer(localPlayer);
                }
            }
        }
        return count;
    }

    public Set<ProtectedRegion> getRegions(UUID id) {
        Set<ProtectedRegion> set = new HashSet<>();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op != null) {
            List<String> worlds = Landlord.getInstance().getConfig().getStringList("disabled-worlds");
            for (World world : Bukkit.getWorlds()) {
                // Only count enabled worlds
                if (!worlds.contains(world.getName())) {
                    set.addAll(getRegions(id, world));
                }
            }
        }
        return set;
    }

    public List<OwnedLand> getRegionsAsOL(UUID id) {
        List<OwnedLand> list = new ArrayList<>();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op != null) {
            List<String> worlds = Landlord.getInstance().getConfig().getStringList("disabled-worlds");

            for (World world : Bukkit.getWorlds()) {
                // Only count enabled worlds
                if (!worlds.contains(world.getName())) {
                    for (ProtectedRegion protectedRegion : getRegions(id, world)) {
                        OwnedLand region = getRegion(protectedRegion);
                        if (region != null) {
                            list.add(region);
                        }
                    }
                }
            }
        }
        return list;
    }

}
