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

    public void claim(Chunk chunk, UUID owner) {
        Location down = chunk.getBlock(0, 0, 0).getLocation();
        Location upper = chunk.getBlock(15, 255, 15).getLocation();

        this.claim(owner, OwnedLand.getName(chunk), chunk.getWorld(), down, upper);
    }

    public void claim(UUID owner, String landname, World world, Location down, Location upper) {
        BlockVector3 vec1 = OwnedLand.locationToVec(down);
        BlockVector3 vec2 = OwnedLand.locationToVec(upper);

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

    public OwnedLand[] getSurroundings(Location ploc) {
        if (ploc == null) return new OwnedLand[0];

        ploc.getWorld().loadChunk(ploc.getChunk().getX() + 1, ploc.getChunk().getZ());
        ploc.getWorld().loadChunk(ploc.getChunk().getX() - 1, ploc.getChunk().getZ());
        ploc.getWorld().loadChunk(ploc.getChunk().getX(), ploc.getChunk().getZ() + 1);
        ploc.getWorld().loadChunk(ploc.getChunk().getX(), ploc.getChunk().getZ() - 1);

        return new OwnedLand[]{
                getRegion(ploc),
                getRegion(ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ())),
                getRegion(ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ())),
                getRegion(ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() + 1)),
                getRegion(ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() - 1)),
        };
    }

    public RegionContainer getRegionContainer() {
        return wg.getPlatform().getRegionContainer();
    }

    public OwnedLand getRegion(ProtectedRegion pr) {

        World w = getWorld(pr.getId());
        int x = getX(pr.getId());
        int z = getZ(pr.getId());

        if (w != null && x != Integer.MIN_VALUE && z != Integer.MIN_VALUE) {
            Chunk c = w.getChunkAt(x, z);
            return new OwnedLand(pr, c);
        }

        return null;
    }

    public Collection<ProtectedRegion> getRegions(World w) {
        com.sk89q.worldedit.world.World worldByName = getWg().getPlatform().getMatcher().getWorldByName(w.getName());
        return Objects.requireNonNull(getRegionContainer().get(worldByName)).getRegions().values();
    }

    public OwnedLand getRegion(String name) {
        return getRegion(getRegionAsPr(name));
    }

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

    public ProtectedCuboidRegion setDefaultFlags(ProtectedCuboidRegion region, Chunk chunk) {
        OwnedLand land = new OwnedLand(region, chunk);
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
        com.sk89q.worldedit.world.World worldByName = getWg().getPlatform().getMatcher().getWorldByName(world.getName());
        RegionContainer regionContainer = getRegionContainer();
        return regionContainer.get(worldByName);
    }

    public boolean canClaim(Player player, Chunk currChunk) {
        RegionManager regionManager = getRegionManager(player.getWorld());
        if (regionManager != null) {
            Vector v1 = currChunk.getBlock(0, 0, 0).getLocation().toVector();
            Vector v2 = currChunk.getBlock(15, 127, 15).getLocation().toVector();

            ProtectedRegion check = new ProtectedCuboidRegion("check",
                    BlockVector3.at(v1.getX(), v1.getY(), v1.getZ()),
                    BlockVector3.at(v2.getX(), v2.getY(), v2.getZ()));
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
