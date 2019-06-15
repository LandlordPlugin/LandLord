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

    IOwnedLand getRegion(Chunk chunk);

    IOwnedLand getRegion(Location loc);

    IOwnedLand getRegion(String name);

    Set<IOwnedLand> getRegions(UUID id, World world);

    Set<IOwnedLand> getRegions(UUID id);

    Set<IOwnedLand> getRegions(World world);

    Set<IOwnedLand> getRegions();

    Set<?> getAllWGRegions(World world);

    Set<?> getAllWGRegions();

    IOwnedLand[] getSurroundings(Location ploc);

    /**
     * Returns 4 direct adjacents protectedlands to pr
     * toReturn[0] = self
     * toReturn[1] = North
     * toReturn[2] = East
     * toReturn[3] = South
     * toReturn[4] = West
     */
    IOwnedLand[] getSurroundings(Chunk chunk);

    IOwnedLand[] getSurroundings(IOwnedLand land);

    IOwnedLand[] getSurroundingsOwner(Location ploc, UUID owner);

    /**
     * Returns all the adjacent lands, that are owned by the same person as pr.
     * If a chunk is from a different owner, null will be placed inside the array.
     * <p>
     * toReturn[0] = self
     * toReturn[1] = North
     * toReturn[2] = East
     * toReturn[3] = South
     * toReturn[4] = West
     *
     * @param chunk the chunk to get adjacent of same owner from
     * @return an array containing all  the nearby lands
     */
    IOwnedLand[] getSurroundingsOwner(Chunk chunk, UUID owner);

    IOwnedLand[] getSurroundingsOwner(IOwnedLand land, UUID owner);

    Map<Chunk, IOwnedLand> getNearbyLands(Location loc, int offsetX, int offsetZ);

    int getRegionCount(UUID id);

    int getRegionCount(World w);

    void unclaim(IOwnedLand land);

    void unclaim(World world, String regionname);

    boolean canClaim(Player player, Chunk currChunk);

    IOwnedLand claim(Chunk chunk, UUID owner);

    World getWorld(String id);

    int getX(String name);

    int getZ(String name);

    boolean isLLRegion(String name);

    String getLandName(Chunk chunk);

    boolean isAllowedInOverlap(Player p, Location loc);

    void highlightLand(Chunk chunk, Player p, Particle particle, int amount);

    String formatLocation(Chunk chunk);

}
