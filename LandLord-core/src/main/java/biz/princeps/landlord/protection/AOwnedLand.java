package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class AOwnedLand implements IOwnedLand {

    private static final String NAMES_DELIMITER = ", ";

    protected final World world;
    protected final ILandLord pl;

    protected final String name;
    protected final int chunkX;
    protected final int chunkZ;

    public AOwnedLand(ILandLord pl, World world, String name) {
        this.world = world;
        this.pl = pl;

        IWorldGuardManager wg = pl.getWGManager();
        this.name = name;
        this.chunkX = wg.getX(name);
        this.chunkZ = wg.getZ(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getChunkX() {
        return chunkX;
    }

    @Override
    public int getChunkZ() {
        return chunkZ;
    }

    /**
     * Highlights the border around the chunk the player is standing in with a particle effect.
     *
     * @param p     player
     * @param chunk the chunk to highlight
     * @param pa    the particle effects to use
     */
    @Override
    public void highlightLand(Chunk chunk, Player p, Particle pa) {
        highlightLand(chunk, p, pa, 1);
    }

    /**
     * Highlights the border around the chunk the player is standing in with a particle effect.
     *
     * @param p player
     * @param e effect to play
     */
    @Override
    public void highlightLand(Player p, Particle e) {
        highlightLand(p.getLocation().getChunk(), p, e, 1);
    }

    @Override
    public void highlightLand(Chunk chunk, Player p, Particle e, int amt) {
        this.pl.getWGManager().highlightLand(chunk, p, e, amt, false);
    }

    /**
     * Gets a location in a chunk on one of the corners. Its the highest possible location.
     * Also loads the chunk
     *
     * @return returns a location on top of the chunk
     */
    @Override
    public Location getALocation() {
        IWorldGuardManager wg = pl.getWGManager();
        World world = wg.getWorld(name);
        if (world == null)
            return null;

        return new Location(world, chunkX << 4, world.getHighestBlockYAt(chunkX << 4, chunkZ << 4) + 1, chunkZ << 4);

    }

    /**
     * Gets a chunk based on the landname. Fetches the chunk from the land name and loads the chunk.
     *
     * @return the chunk of the land
     */
    @Override
    public Chunk getChunk() {
        IWorldGuardManager wg = pl.getWGManager();
        World w = wg.getWorld(name);

        if (w != null && chunkX != Integer.MIN_VALUE && chunkZ != Integer.MIN_VALUE) {
            return w.getChunkAt(chunkX, chunkZ);
        }
        return null;
    }

    @Override
    public boolean contains(Location loc) {
        return this.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public abstract ILLFlag getFlag(String s);

    protected static String formatNames(Iterable<UUID> uuids) {
        StringJoiner stringJoiner = new StringJoiner(NAMES_DELIMITER);
        // ugly, maybe solve this in the future
        for (UUID uuid : uuids) {
            stringJoiner.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return stringJoiner.toString();
    }

}
