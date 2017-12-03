package biz.princeps.landlord.handler;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

/**
 * Created by spatium on 17.07.17.
 */
public class WorldGuardHandler {

    private WorldGuardPlugin wg;

    public WorldGuardHandler(WorldGuardPlugin wg) {
        this.wg = wg;
    }

    public void claim(Chunk chunk, UUID owner) {
        Location down = chunk.getBlock(0, 0, 0).getLocation();
        Location upper = chunk.getBlock(15, 256, 15).getLocation();

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

        RegionManager manager = wg.getRegionContainer().get(world);

        manager.addRegion(pr);
    }

    public OwnedLand getRegion(Chunk chunk) {
        RegionManager manager = wg.getRegionContainer().get(chunk.getWorld());
        ProtectedRegion pr = manager.getRegion(OwnedLand.getName(chunk));
        return (pr != null ? new OwnedLand(pr, chunk) : null);
    }

    public OwnedLand getRegion(Location loc) {
        return getRegion(loc.getChunk());
    }

    public OwnedLand getRegion(ProtectedRegion pr) {
        String name = pr.getId();
        String[] splitted = name.split("_");

        if (splitted.length != 3) {
            return null;
        }

        World world = Bukkit.getWorld(splitted[0]);
        if (world == null)
            return null;

        try {
            int x = Integer.parseInt(splitted[1]);
            int z = Integer.parseInt(splitted[2]);
            Chunk chunk = world.getChunkAt(x, z);
            return getRegion(chunk);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void unclaim(World world, String regionname) {
        wg.getRegionManager(world).removeRegion(regionname);
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
        for (Flag<?> flag : DefaultFlag.getFlags()) {
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

    public WorldGuardPlugin getWG() {
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

    public List<ProtectedRegion> getRegions(UUID id, World world) {
        List<ProtectedRegion> regions = new ArrayList<>();
        for (ProtectedRegion protectedRegion : this.getWG().getRegionManager(world).getRegions().values()) {
            if (protectedRegion.getOwners().getUniqueIds().contains(id))
                regions.add(protectedRegion);
        }
        return regions;
    }

    public boolean canClaim(Player player, Chunk currChunk) {
        RegionManager regionManager = wg.getRegionManager(player.getWorld());
        if (regionManager != null) {
            ProtectedRegion check = new ProtectedCuboidRegion("check", toVector(currChunk.getBlock(0, 0, 0)), toVector(currChunk.getBlock(15, 127, 15)));
            List<ProtectedRegion> intersects = check.getIntersectingRegions(new ArrayList<>(regionManager.getRegions().values()));
            for (ProtectedRegion intersect : intersects) {
                if (!regionManager.getApplicableRegions(intersect).canBuild(wg.wrapPlayer(player))) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getRegionCountOfPlayer(UUID id) {
        int count = 0;
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op != null)
            for (World world : Bukkit.getWorlds()) {
                // Only count enabled worlds
                if (!Landlord.getInstance().getConfig().getStringList("disabled-worlds").contains(world.getName()))
                    count += getWG().getRegionManager(world).getRegionCountOfPlayer(getWG().wrapOfflinePlayer(op));
            }
        return count;
    }

    public Set<ProtectedRegion> getRegions(UUID id) {
        Set<ProtectedRegion> set = new HashSet<>();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op != null)
            for (World world : Bukkit.getWorlds()) {
                // Only count enabled worlds
                if (!Landlord.getInstance().getConfig().getStringList("disabled-worlds").contains(world.getName()))
                    set.addAll(getRegions(id, world));
            }
        return set;
    }

    public List<OwnedLand> getRegionsAsOL(UUID id) {
        List<OwnedLand> list = new ArrayList<>();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op != null)
            for (World world : Bukkit.getWorlds()) {
                // Only count enabled worlds
                if (!Landlord.getInstance().getConfig().getStringList("disabled-worlds").contains(world.getName()))
                    for (ProtectedRegion protectedRegion : getRegions(id, world)) {
                        list.add(getRegion(protectedRegion));
                    }
            }
        return list;
    }

}
