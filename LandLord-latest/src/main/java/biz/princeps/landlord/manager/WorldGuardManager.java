package biz.princeps.landlord.manager;

import biz.princeps.landlord.OwnedLand;
import biz.princeps.landlord.api.ClaimHeightDefinition;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.tuple.Pair;
import biz.princeps.landlord.protection.AWorldGuardManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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
    private final WorldGuard wg;

    public WorldGuardManager(ILandLord plugin, WorldGuardPlugin worldGuard) {
        super(plugin);
        this.wg = WorldGuard.getInstance();
        this.wgPlugin = worldGuard;
    }

    public void initCache() {
        for (World world : plugin.getServer().getWorlds()) {
            RegionManager manager = getRegionManager(world);
            for (ProtectedRegion value : manager.getRegions().values()) {
                if (isLLRegion(value.getId())) {
                    cache.add(OwnedLand.of(plugin, value));
                }
            }
        }
    }

    public static void initFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            // Register our flag with the registry.
            registry.register(REGION_PRICE_FLAG);
        } catch (FlagConflictException e) {
            // Some other plugin registered a flag by the same name already.
            // You may want to re-register with a different name, but this
            // could cause issues with saved flags in region files. If you don't mind
            // sharing a flag, consider making your field non-final and assigning it.
        }
    }

    /**
     * Claims a chunk for a player in worldguard by selecting points according to configuration.
     */
    @Override
    public IOwnedLand claim(Chunk chunk, UUID owner) {
        World world = chunk.getWorld();
        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;
        Pair<Integer, Integer> boundaries = calcClaimHeightBoundaries(chunk);
        Location down = new Location(world, x, boundaries.getLeft(), z);
        Location upper = new Location(world, x + 15, boundaries.getRight(), z + 15);

        BlockVector3 vec1 = locationToVec(down);
        BlockVector3 vec2 = locationToVec(upper);
        ProtectedCuboidRegion pr = new ProtectedCuboidRegion(getLandName(chunk), vec1, vec2);

        RegionManager manager = getRegionManager(chunk.getWorld());
        if (manager != null) {
            IOwnedLand land;
            if (manager.hasRegion(pr.getId())) {
                // Don't init flags and data for an existing land, old data will be copied out later during reclaim.
                land = OwnedLand.of(plugin, pr);
            } else {
                land = OwnedLand.create(plugin, pr, owner);
            }
            manager.addRegion(pr);
            pr.getOwners().addPlayer(owner);
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
        for (World world : plugin.getServer().getWorlds()) {
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
        for (World world : plugin.getServer().getWorlds()) {
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
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Checks for overlapping regions. Returns false if there is another overlapping region.
     */
    @Override
    public boolean canClaim(Player player, Chunk currChunk) {
        RegionManager regionManager = getRegionManager(player.getWorld());
        if (regionManager == null) {
            return false;
        }
        int x = currChunk.getX() << 4;
        int z = currChunk.getZ() << 4;
        Vector v1 = new Location(currChunk.getWorld(), x, 0, z).toVector();
        Vector v2 = new Location(currChunk.getWorld(), x + 15, 255, z + 15).toVector();

        ProtectedRegion check = new ProtectedCuboidRegion("check",
                BlockVector3.at(v1.getX(), v1.getY(), v1.getZ()),
                BlockVector3.at(v2.getX(), v2.getY(), v2.getZ()));
        List<ProtectedRegion> intersects = check
                .getIntersectingRegions(new ArrayList<>(regionManager.getRegions().values()));
        for (ProtectedRegion intersect : intersects) {
            // check this out, might not work. canBuild was removed in 1.13.1 and Im not sure if isMemberOfAll is
            // equivalent
            // 10/26/18 looks like its working:
            if (!regionManager.getApplicableRegions(intersect).isMemberOfAll(wgPlugin.wrapPlayer(player))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void moveUp(World world, int chunkX, int chunkZ, int amount) {
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        int x = chunkX << 4;
        int z = chunkZ << 4;
        Vector v1 = new Location(world, x, 3, z).toVector();
        Vector v2 = new Location(world, x + 15, 255, z + 15).toVector();

        BlockVector3 b1 = BlockVector3.at(v1.getX(), v1.getY(), v1.getZ());
        BlockVector3 b2 = BlockVector3.at(v2.getX(), v2.getY(), v2.getZ());

        CuboidRegion region = new CuboidRegion(weWorld, b1, b2);

        try {
            region.shift(BlockVector3.at(0, amount, 0));
        } catch (RegionOperationException e) {
            e.printStackTrace();
        }
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
        return wg.getPlatform().getRegionContainer();
    }

    private RegionManager getRegionManager(World world) {
        com.sk89q.worldedit.world.World worldByName = wg.getPlatform().getMatcher().getWorldByName(world.getName());
        RegionContainer regionContainer = getRegionContainer();
        return regionContainer.get(worldByName);
    }

    private BlockVector3 locationToVec(Location loc) {
        return BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
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

    @Override
    public Pair<Integer, Integer> calcClaimHeightBoundaries(Chunk chunk) {
        ClaimHeightDefinition boundaryMethod = ClaimHeightDefinition.parse(plugin.getConfig().getString("ClaimHeight.method"));

        // We will use the full and default behaviour as default value.
        if (boundaryMethod == null) {
            boundaryMethod = ClaimHeightDefinition.FULL;
        }

        World world = chunk.getWorld();
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        int maxHeight = weWorld.getMaxY();
        int minHeight = weWorld.getMinY();

        // Full is the default behaviour.
        // This will claim the whole chunk.
        if (boundaryMethod == ClaimHeightDefinition.FULL) {
            return Pair.of(minHeight, maxHeight);
        }

        String claimHeightWorldType = world.getEnvironment() == World.Environment.NORMAL ? "overworld" : "other";
        int bottomY = plugin.getConfig().getInt("ClaimHeight." + claimHeightWorldType + "-bottomY", minHeight);
        int topY = plugin.getConfig().getInt("ClaimHeight." + claimHeightWorldType + "-topY", maxHeight);

        // Fixed is the simple claim behaviour.
        // We want to handle this first.
        if (boundaryMethod == ClaimHeightDefinition.FIXED) {
            return Pair.of(Math.max(minHeight, bottomY), Math.min(topY, maxHeight));
        }

        // Let's find all highest points in the chunk.
        List<Integer> points = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                points.add(world.getHighestBlockYAt((chunk.getX() << 4) + x, (chunk.getZ() << 4) + z));
            }
        }

        // Get the center based on the boundary Method
        int center = boundaryMethod.getCenter(points);

        bottomY = center + bottomY;
        topY = center + topY;

        if (plugin.getConfig().getBoolean("ClaimHeight.appendOversize")) {
            // We append the oversize which reach out of the world on the top or the bottom if it fits.
            // We throw oversize away if we would exceed the world height limit on both ends.
            if (topY > maxHeight) {
                bottomY -= topY - maxHeight;
                topY = maxHeight;
                if (bottomY < minHeight) {
                    bottomY = minHeight;
                }
            }

            if (bottomY < minHeight) {
                topY += Math.abs(bottomY);
                bottomY = minHeight;
                if (topY > maxHeight) {
                    topY = maxHeight;
                }
            }
        } else {
            // Just clamp this stuff.
            bottomY = Math.max(bottomY, minHeight);
            topY = Math.min(topY, maxHeight);
        }
        return Pair.of(bottomY, topY);
    }

}
