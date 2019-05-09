package biz.princeps.landlord;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.handler.AWorldGuardProxy;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class WorldGuardProxy extends AWorldGuardProxy {

    private WorldGuardPlugin wgPlugin;
    private WorldGuard wg;

    public WorldGuardProxy(ILandLord pl, WorldGuardPlugin worldGuard) {
        super(pl);
        this.wg = WorldGuard.getInstance();
        this.wgPlugin = worldGuard;
    }

    //TODO check performance of sync loading
    void initCache() {
        for (World world : Bukkit.getWorlds()) {
            RegionManager manager = getRegionManager(world);
            for (ProtectedRegion value : manager.getRegions().values()) {
                if (isLLRegion(value.getId())) {
                    cache.add(OwnedLand.of(pl, value));
                }
            }
        }
    }

    /**
     * Claims a chunk for a player in worldguard by selecting the most bottom and the highest point
     */
    @Override
    public IOwnedLand claim(Chunk chunk, UUID owner) {
        Location down = chunk.getBlock(0, 0, 0).getLocation();
        Location upper = chunk.getBlock(15, 255, 15).getLocation();

        BlockVector3 vec1 = locationToVec(down);
        BlockVector3 vec2 = locationToVec(upper);

        ProtectedCuboidRegion pr = new ProtectedCuboidRegion(getLandName(chunk), vec1, vec2);

        RegionManager manager = getRegionManager(chunk.getWorld());
        if (manager != null) {
            manager.addRegion(pr);
            OwnedLand land = OwnedLand.create(pl, pr, owner);
            land.replaceOwner(owner);
            cache.add(land);
            return land;
        }
        return null;
    }

    @Override
    public IOwnedLand getRegion(Chunk chunk) {
        String name = getLandName(chunk);
        return getRegion(name);
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
        return cache.getLand(name);
    }

    @Override
    public Set<IOwnedLand> getRegions(World world) {
        return cache.getLands(world);
    }

    @Override
    public Set<IOwnedLand> getRegions(UUID id, World world) {
        Set<IOwnedLand> lands = cache.getLands(id);
        return lands.stream().filter(l -> l.getWorld().equals(world)).collect(Collectors.toSet());
    }

    @Override
    public Set<IOwnedLand> getRegions(UUID id) {
        return cache.getLands(id);
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
    public void unclaim(IOwnedLand land) {
        unclaim(land.getWorld(), land.getName());
    }

    @Override
    public void unclaim(World world, String regionname) {
        this.cache.remove(regionname);
        getRegionManager(world).removeRegion(regionname);
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
     * @param id the uuid of the player to get the region count for
     * @return the region count
     */
    @Override
    public int getRegionCount(UUID id) {
        if (cache.getLands(id) == null) return 0;
        return cache.getLands(id).size();
    }

    @Override
    public int getRegionCount(World w) {
        if (cache.getLands(w) == null) return 0;
        return cache.getLands(w).size();
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
                .getApplicableRegions(localPlayer.getLocation().toVector().toBlockPoint());
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
