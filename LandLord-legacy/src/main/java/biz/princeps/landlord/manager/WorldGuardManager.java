package biz.princeps.landlord.manager;

import biz.princeps.landlord.OwnedLand;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.tuple.Pair;
import biz.princeps.landlord.protection.AWorldGuardManager;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Project: LandLord Created by Alex D. (SpatiumPrinceps) Date: 06-05-19
 */
public class WorldGuardManager extends AWorldGuardManager {
    public static final DoubleFlag REGION_PRICE_FLAG = new DoubleFlag("region-price");

    private final WorldGuardPlugin wgPlugin;

    public WorldGuardManager(ILandLord plugin, WorldGuardPlugin worldGuard) {
        super(plugin);
        this.wgPlugin = worldGuard;
    }

    public void initCache() {
        for (World world : plugin.getPlugin().getServer().getWorlds()) {
            RegionManager manager = getRegionManager(world);
            for (ProtectedRegion value : manager.getRegions().values()) {
                if (isLLRegion(value.getId())) {
                    cache.add(OwnedLand.of(plugin, value));
                }
            }
        }
    }

    public static void initFlags(WorldGuardPlugin wgplugin) {
        FlagRegistry registry = wgplugin.getFlagRegistry();
        try {
            // register our flag with the registry
            registry.register(REGION_PRICE_FLAG);
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you may want to re-register with a different name, but this
            // could cause issues with saved flags in region files. if you don't mind
            // sharing a flag, consider making your field non-final and assigning it:
        }
    }

    /**
     * Claims a chunk for a player in worldguard by selecting the most bottom and the highest point
     */
    @Override
    public IOwnedLand claim(Chunk chunk, UUID owner) {
        World world = chunk.getWorld();
        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;
        Pair<Integer, Integer> boundaries = calcClaimHeightBoundaries(chunk);
        Location down = new Location(world, x, boundaries.getLeft(), z);
        Location upper = new Location(world, x + 15, boundaries.getRight(), z + 15);

        BlockVector vec1 = locationToVec(down);
        BlockVector vec2 = locationToVec(upper);
        ProtectedCuboidRegion pr = new ProtectedCuboidRegion(getLandName(chunk), vec1, vec2);

        RegionManager manager = getRegionManager(chunk.getWorld());
        if (manager != null) {
            manager.addRegion(pr);
            IOwnedLand land = OwnedLand.create(plugin, pr, owner);
            land.replaceOwner(owner);
            cache.add(land);
            return land;
        }
        return null;
    }

    @Override
    public IOwnedLand getRegion(String name) {
        return cache.getLand(name);
    }

    @Override
    public Set<IOwnedLand> getRegions(World world) {
        return cache.getLands(world);
    }

    @Override
    public Set<IOwnedLand> getRegions() {
        Set<IOwnedLand> lands = new HashSet<>();
        for (World world : plugin.getPlugin().getServer().getWorlds()) {
            lands.addAll(cache.getLands(world));
        }
        return lands;
    }

    @Override
    public Set<?> getAllWGRegions(World world) {
        Map<String, ProtectedRegion> regions = new HashMap<>(getRegionManager(world).getRegions());
        for (String r : getRegionManager(world).getRegions().keySet()) {
            if (isLLRegion(r)) {
                regions.remove(r);
            }
        }
        return new HashSet<>(regions.values());
    }

    @Override
    public Set<?> getAllWGRegions() {
        Set<ProtectedRegion> set = new HashSet<>();
        for (World world : plugin.getPlugin().getServer().getWorlds()) {
            Set<?> allWGRegions = getAllWGRegions(world);
            set.addAll(((Set<ProtectedRegion>) allWGRegions));
        }
        return set;
    }

    @Override
    public Set<IOwnedLand> getRegions(UUID id, World world) {
        Set<IOwnedLand> lands = new HashSet<>();
        for (IOwnedLand land : cache.getLands(id)) {
            if (land.getWorld() != world)
                continue;

            lands.add(land);
        }
        return lands;
    }

    @Override
    public Set<IOwnedLand> getRegions(UUID id) {
        return cache.getLands(id);
    }

    @Override
    public void unclaim(IOwnedLand land) {
        unclaim(land.getWorld(), land.getName());
    }

    @Override
    public void unclaim(World world, String regionname) {
        this.cache.remove(regionname);
        new BukkitRunnable() {
            @Override
            public void run() {
                getRegionManager(world).removeRegion(regionname);
            }
        }.runTaskAsynchronously(plugin.getPlugin());
    }

    /**
     * Checks for overlapping regions. Returns false if there is another overlapping region.
     */
    @Override
    public boolean canClaim(Player player, Chunk currChunk) {
        RegionManager regionManager = getRegionManager(player.getWorld());
        if (regionManager != null) {
            int x = currChunk.getX() << 4;
            int z = currChunk.getZ() << 4;
            Vector v1 = new Location(currChunk.getWorld(), x, 0, z).toVector();
            Vector v2 = new Location(currChunk.getWorld(), x + 15, 255, z + 15).toVector();

            ProtectedRegion check = new ProtectedCuboidRegion("check",
                    new BlockVector(v1.getX(), v1.getY(), v1.getZ()),
                    new BlockVector(v2.getX(), v2.getY(), v2.getZ()));
            List<ProtectedRegion> intersects = check
                    .getIntersectingRegions(new ArrayList<>(regionManager.getRegions().values()));
            for (ProtectedRegion intersect : intersects) {
                if (!regionManager.getApplicableRegions(intersect).isMemberOfAll(wgPlugin.wrapPlayer(player))) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void moveUp(World world, int chunkX, int chunkZ, int amt) {
        int x = chunkX << 4;
        int z = chunkZ << 4;
        Vector v1 = new Location(world, x, 3, z).toVector();
        Vector v2 = new Location(world, x + 15, 255, z + 15).toVector();

        BlockVector b1 = BlockVector.toBlockPoint(v1.getX(), v1.getY(), v1.getZ());
        BlockVector b2 = BlockVector.toBlockPoint(v2.getX(), v2.getY(), v2.getZ());

        CuboidRegion region = new CuboidRegion(getWGWorld(world.getName()), b1, b2);
        try {
            region.shift(BlockVector.toBlockPoint(0, amt, 0));
        } catch (RegionOperationException e) {
            e.printStackTrace();
        }
    }

    public com.sk89q.worldedit.world.World getWGWorld(String name) {
        for (com.sk89q.worldedit.world.World world : WorldEdit.getInstance().getServer().getWorlds()) {
            if (world.getName().equals(name)) {
                return world;
            }
        }
        return null;
    }

    /**
     * @param id the uuid of the player to get the region count for
     * @return the region count
     */
    @Override
    public int getRegionCount(UUID id) {
        return cache.getLands(id).size();
    }

    /**
     * @param id the uuid of the player to get the region count for
     * @return the region count
     */
    @Override
    public int getRegionCount(UUID id, World world) {
        return (int) cache.getLands(id).stream()
                .filter(ownedLand -> ownedLand.getWorld().equals(world)).count();
    }

    @Override
    public int getRegionCount(World world) {
        return cache.getLands(world).size();
    }

    private RegionContainer getRegionContainer() {
        return wgPlugin.getRegionContainer();
    }

    private RegionManager getRegionManager(World world) {
        RegionContainer regionContainer = getRegionContainer();
        return regionContainer.get(world);
    }

    private BlockVector locationToVec(Location loc) {
        return new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public boolean isAllowedInOverlap(Player p, Location loc) {
        LocalPlayer localPlayer = wgPlugin.wrapPlayer(p);
        ApplicableRegionSet applicableRegions = getRegionManager(loc.getWorld())
                .getApplicableRegions(locationToVec(loc));
        if (applicableRegions.getRegions().size() > 0) { // check for other lands, that may not be handled by landlord
            for (ProtectedRegion protectedRegion : applicableRegions.getRegions()) {
                if (protectedRegion.isMember(localPlayer) || protectedRegion.isOwner(localPlayer)) {
                    return true;
                }
            }
        }
        return false;
    }

}
