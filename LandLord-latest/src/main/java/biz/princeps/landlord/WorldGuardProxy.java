package biz.princeps.landlord;

import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.handler.AWorldGuardProxy;
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
 * Date: 06-05-19
 */
public class WorldGuardProxy extends AWorldGuardProxy {

    private WorldGuardPlugin wgPlugin;
    private WorldGuard wg;

    public WorldGuardProxy(WorldGuardPlugin worldGuard) {
        this.wg = WorldGuard.getInstance();
        this.wgPlugin = worldGuard;
    }

    /**
     * Claims a chunk for a player in worldguard by selecting the most bottom and the highest point
     */
    @Override
    public void claim(Chunk chunk, UUID owner) {
        Location down = chunk.getBlock(0, 0, 0).getLocation();
        Location upper = chunk.getBlock(15, 255, 15).getLocation();

        BlockVector3 vec1 = locationToVec(down);
        BlockVector3 vec2 = locationToVec(upper);

        ProtectedCuboidRegion pr = new ProtectedCuboidRegion(getLandName(chunk), vec1, vec2);

        DefaultDomain ownerDomain = new DefaultDomain();
        ownerDomain.addPlayer(owner);
        pr.setOwners(ownerDomain);

        // flag management
        setDefaultFlags(pr);
        RegionManager manager = getRegionManager(chunk.getWorld());

        if (manager != null) {
            manager.addRegion(pr);
        }
    }

    @Override
    public IOwnedLand getRegion(Chunk chunk) {
        RegionManager manager = getRegionManager(chunk.getWorld());
        ProtectedRegion pr = manager != null ? manager.getRegion(getLandName(chunk)) : null;
        return (pr != null ? new OwnedLand(pr) : null);
    }

    @Override
    public IOwnedLand getRegion(Location loc) {
        String name = loc.getWorld().getName().replace(" ", "_");
        name += "_";

        // x coord
        int x = (int) Math.floor(loc.getX() / 16);
        name += x;
        name += "_";
        // z coord
        int z = (int) Math.floor(loc.getZ() / 16);
        name += z;
        return getRegion(name);
    }

    @Override
    public IOwnedLand getRegion(String name) {
        return null;
    }

    /**
     * Return the surrounding protected regions of a location.
     *
     * @param ploc the location
     * @return an array of size 5 containing the region of the location itsself and all the surrounding regions
     */
    @Override
    public IOwnedLand[] getSurroundings(Location ploc) {
        if (ploc == null) return new OwnedLand[0];
        return new IOwnedLand[]{
                getRegion(ploc),
                getRegion(ploc.clone().add(16, 0, 0)),
                getRegion(ploc.clone().subtract(16, 0, 0)),
                getRegion(ploc.clone().add(0, 0, 16)),
                getRegion(ploc.clone().subtract(0, 0, 16)),
        };
    }

    @Override
    public Collection<IOwnedLand> getRegions(World world) {
        com.sk89q.worldedit.world.World worldByName = wg.getPlatform().getMatcher().getWorldByName(world.getName());
        RegionManager regionManager = getRegionContainer().get(worldByName);
        Set<IOwnedLand> set = new HashSet<>();

        if (regionManager != null) {
            for (ProtectedRegion value : regionManager.getRegions().values()) {
                set.add(new OwnedLand(value));
            }
        }
        return set;
    }

    @Override
    public void unclaim(World world, String regionname) {
        getRegionManager(world).removeRegion(regionname);
    }

    private void setDefaultFlags(ProtectedRegion region) {
        if (region == null) return;

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
    }

    @Override
    public List<IOwnedLand> getRegions(UUID id, World world) {
        List<IOwnedLand> regions = new ArrayList<>();
        for (ProtectedRegion protectedRegion : getRegionManager(world).getRegions().values()) {
            if (protectedRegion.getOwners().getUniqueIds().contains(id)) {
                regions.add(new OwnedLand(protectedRegion));
            }
        }
        return regions;
    }

    /**
     * Checks for overlapping regions.
     * Returns false if there is another overlapping region.
     * TODO figure out the 127 in vector2. My intuition tells me, that its wrong.
     */
    @Override
    public boolean canClaim(Player player, Chunk currChunk) {
        RegionManager regionManager = getRegionManager(player.getWorld());
        if (regionManager != null) {
            org.bukkit.util.Vector v1 = currChunk.getBlock(0, 0, 0).getLocation().toVector();
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

    /**
     * Produces wrong numbers if a player owns nonllregions in a world
     * // TODO fix this
     *
     * @param id the uuid of the player to get the region count for
     * @return the region count
     */
    @Override
    public int getRegionCountOfPlayer(UUID id) {
        int count = 0;
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        List<String> worlds = Landlord.getInstance().getConfig().getStringList("disabled-worlds");

        for (World world : Bukkit.getWorlds()) {
            // Only count enabled worlds
            if (!worlds.contains(world.getName())) {
                RegionManager rm = getRegionManager(world);
                LocalPlayer localPlayer = wgPlugin.wrapOfflinePlayer(op);
                count += rm.getRegionCountOfPlayer(localPlayer);
            }
        }
        return count;
    }


    private RegionContainer getRegionContainer() {
        return wg.getPlatform().getRegionContainer();
    }

    private RegionManager getRegionManager(World world) {
        com.sk89q.worldedit.world.World worldByName = wg.getPlatform().getMatcher().getWorldByName(world.getName());
        RegionContainer regionContainer = getRegionContainer();
        return regionContainer.get(worldByName);
    }

    public String getLandName(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    public BlockVector3 locationToVec(Location loc) {
        return BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
