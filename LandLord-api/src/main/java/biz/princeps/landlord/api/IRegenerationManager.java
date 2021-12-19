package biz.princeps.landlord.api;

import org.bukkit.Location;
import org.bukkit.World;

public interface IRegenerationManager {

    /**
     * Regenerates a chunk by its coordinates.
     *
     * @param world the world the chunk to regenerate is in.
     * @param x     the x axis component of the chunk's position.
     * @param z     the z axis component of the chunk's position.
     */
    void regenerateChunk(World world, int x, int z);

    /**
     * Regenerates the chunk the given location is inside of.
     *
     * @param location a location inside of the chunk to regenerate.
     */
    default void regenerateChunk(Location location) {
        regenerateChunk(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }
}
