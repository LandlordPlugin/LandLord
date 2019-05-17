package biz.princeps.landlord.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public interface IWorldGuardManager {

    // Abstract WorldGuardManager
    ILand getRegion(Chunk chunk);

    ILand getRegion(String name);

    Set<IPossessedLand> getRegions(UUID id, World world);

    Set<IPossessedLand> getRegions(UUID id);

    Set<IPossessedLand> getRegions(World world);

    Set<IPossessedLand> getRegions();

    int getRegionCount(UUID id);

    int getRegionCount(World w);

    String getChunkName(Chunk chunk);

    // Concrete WorldGuardMangaer

    ILand getRegion(Location loc);

    Set<?> getAllWGRegions(World world);

    Set<?> getAllWGRegions();

    void initFlags();
}
