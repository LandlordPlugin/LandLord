package biz.princeps.landlord.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public interface IWorldGuardProxy {
    void claim(Chunk chunk, UUID owner);

    IOwnedLand getRegion(Chunk chunk);

    IOwnedLand getRegion(Location loc);

    IOwnedLand getRegion(String name);

    IOwnedLand[] getSurroundings(Location ploc);

    Collection<IOwnedLand> getRegions(World world);

    void unclaim(World world, String regionname);

    List<IOwnedLand> getRegions(UUID id, World world);

    boolean canClaim(Player player, Chunk currChunk);

    int getRegionCountOfPlayer(UUID id);

    Set<IOwnedLand> getRegions(UUID id);

    Map<Chunk, IOwnedLand> getNearbyLands(Location loc, int offsetX, int offsetZ);

    World getWorld(String id);

    int getX(String name);

    int getY(String name);

    int getZ(String name);
}
