package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ILand;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IPossessedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class AWorldGuardManager implements IWorldGuardManager {

    protected LandCache cache = new LandCache();
    protected ILandLord pl;

    public AWorldGuardManager(ILandLord pl) {
        this.pl = pl;
    }

    @Override
    public ILand getRegion(Chunk chunk) {
        return getRegion(chunk.getBlock(1, 1, 1).getLocation());
    }

    @Nullable
    @Override
    public ILand getRegion(String name) {
        return cache.getLand(name);
    }

    @Override
    public Set<IPossessedLand> getRegions(UUID id, World w) {
        return cache.getLands(id).stream().filter(p -> p.getWorld().equals(w)).collect(Collectors.toSet());
    }

    @Override
    public Set<IPossessedLand> getRegions(UUID id) {
        return cache.getLands(id);
    }

    @Override
    public Set<IPossessedLand> getRegions(World w) {
        return cache.getLands(w);
    }

    @Override
    public Set<IPossessedLand> getRegions() {
        return cache.getLands();
    }

    /**
     * @param id the uuid of the player to get the region count for
     * @return the region count
     */
    @Override
    public int getRegionCount(UUID id) {
        return cache.getLands(id).size();
    }

    @Override
    public int getRegionCount(World w) {
        return cache.getLands(w).size();
    }

    @Override
    public String getChunkName(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }


}
