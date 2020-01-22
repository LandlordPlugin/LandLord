package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class AOwnedLand implements IOwnedLand {

    protected World world;
    protected ILandLord pl;

    public AOwnedLand(ILandLord pl, World world) {
        this.world = world;
        this.pl = pl;
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
        World world = wg.getWorld(getName());
        if (world == null)
            return null;

        int x = wg.getX(getName());
        int z = wg.getZ(getName());
        return new Location(world, x * 16, world.getHighestBlockYAt(x * 16, z * 16) + 1, z * 16);

    }

    /**
     * Gets a chunk based on the landname. Fetches the chunk from the land name and loads the chunk.
     *
     * @return the chunk of the land
     */
    @Override
    public Chunk getChunk() {
        IWorldGuardManager wg = pl.getWGManager();
        World w = wg.getWorld(getName());
        int x = wg.getX(getName());
        int z = wg.getZ(getName());

        if (w != null && x != Integer.MIN_VALUE && z != Integer.MIN_VALUE) {
            return w.getChunkAt(x, z);
        }
        return null;
    }

    @Override
    public boolean contains(Location loc) {
        return this.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
