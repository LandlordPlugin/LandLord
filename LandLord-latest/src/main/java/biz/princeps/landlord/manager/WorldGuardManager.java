package biz.princeps.landlord.manager;

import biz.princeps.landlord.OwnedLand;
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
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;


/**
 * Project: LandLord Created by Alex D. (SpatiumPrinceps) Date: 06-05-19
 */
public class WorldGuardManager extends AWorldGuardManager {

    public static final DoubleFlag REGION_PRICE_FLAG = new DoubleFlag("region-price");

    private final WorldGuardPlugin wgPlugin;
    private final WorldGuard wg;

    public WorldGuardManager(ILandLord pl, WorldGuardPlugin worldGuard) {
        super(pl);
        this.wg = WorldGuard.getInstance();
        this.wgPlugin = worldGuard;
    }

    //TODO check performance of sync loading
    public void initCache() {
        for (World world : Bukkit.getWorlds()) {
            RegionManager manager = getRegionManager(world);
            for (ProtectedRegion value : manager.getRegions().values()) {
                if (isLLRegion(value.getId())) {
                    cache.add(OwnedLand.of(pl, value));
                }
            }
        }
    }

    public static void initFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
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
        final World world = chunk.getWorld();
        final int x = chunk.getX() << 4;
        final int z = chunk.getZ() << 4;
        final Pair<Integer, Integer> boundaries = calcClaimHeightBoundaries(chunk);
        final Location down = new Location(world, x, boundaries.getLeft(), z);
        final Location upper = new Location(world, x + 15, boundaries.getRight(), z + 15);

        final BlockVector3 vec1 = locationToVec(down);
        final BlockVector3 vec2 = locationToVec(upper);
        final ProtectedCuboidRegion pr = new ProtectedCuboidRegion(getLandName(chunk), vec1, vec2);

        final RegionManager manager = getRegionManager(chunk.getWorld());
        if (manager != null) {
            manager.addRegion(pr);
            final IOwnedLand land = OwnedLand.create(pl, pr, owner);
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
        final Set<IOwnedLand> lands = new HashSet<>();
        for (World world : Bukkit.getWorlds()) {
            lands.addAll(cache.getLands(world));
        }
        return lands;
    }

    @Override
    public Set<?> getAllWGRegions(World world) {
        final Map<String, ProtectedRegion> regions = new HashMap<>(getRegionManager(world).getRegions());
        for (String r : getRegionManager(world).getRegions().keySet()) {
            if (isLLRegion(r)) {
                regions.remove(r);
            }
        }
        return new HashSet<>(regions.values());
    }

    @Override
    public Set<?> getAllWGRegions() {
        final Set<ProtectedRegion> set = new HashSet<>();
        for (World world : Bukkit.getWorlds()) {
            final Set<?> allWGRegions = getAllWGRegions(world);
            set.addAll(((Set<ProtectedRegion>) allWGRegions));
        }
        return set;
    }

    @Override
    public Set<IOwnedLand> getRegions(UUID id, World world) {
        final Set<IOwnedLand> lands = new HashSet<>();
        for (IOwnedLand land : cache.getLands(id)) {
            if (land.getWorld() != world) continue;

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
        Bukkit.getScheduler().runTaskAsynchronously(pl.getPlugin(), () -> getRegionManager(world).removeRegion(regionname));
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
        Vector v1 = new Location(currChunk.getWorld(), currChunk.getX() << 4, 0, currChunk.getZ() << 4).toVector();
        Vector v2 = new Location(currChunk.getWorld(), (currChunk.getX() << 4) + 15, 255, (currChunk.getZ() << 4) + 15).toVector();

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
    public void moveUp(World world, int x, int z, int amt) {
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        Chunk chunk = world.getChunkAt(x, z);
        Vector v1 = new Location(chunk.getWorld(), chunk.getX() << 4, 3, chunk.getZ() << 4).toVector();
        Vector v2 = new Location(chunk.getWorld(), (chunk.getX() << 4) + 15, 255, (chunk.getZ() << 4) + 15).toVector();

        BlockVector3 b1 = BlockVector3.at(v1.getX(), v1.getY(), v1.getZ());
        BlockVector3 b2 = BlockVector3.at(v2.getX(), v2.getY(), v2.getZ());

        CuboidRegion region = new CuboidRegion(weWorld, b1, b2);

        try {
            region.shift(BlockVector3.at(0, amt, 0));
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
        final Set<IOwnedLand> lands = cache.getLands(id);
        return lands == null ? 0 : lands.size();
    }

    /**
     * @param id the uuid of the player to get the region count for
     * @return the region count
     */
    @Override
    public int getRegionCount(UUID id, World world) {
        if (cache.getLands(id) == null) return 0;
        return (int) cache.getLands(id).stream().filter(l -> l.getWorld().equals(world)).count();
    }

    @Override
    public int getRegionCount(World w) {
        final Set<IOwnedLand> lands = cache.getLands(w);
        return lands == null ? 0 : lands.size();
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

}
