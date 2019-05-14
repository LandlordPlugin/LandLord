package biz.princeps.landlord.util;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardProxy;
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
        highlightLand(chunk, p, pa, 5);
    }

    /**
     * Highlights the border around the chunk the player is standing in with a particle effect.
     *
     * @param p player
     * @param e effect to play
     */
    @Override
    public void highlightLand(Player p, Particle e) {
        highlightLand(p.getLocation().getChunk(), p, e, 5);
    }


    /**
     * Highlight a specific chunk for a player with particles
     *
     * @param chunk the chunk
     * @param p     the player to highlight the chunk for
     * @param e     the particles
     * @param amt   amount of particles
     */
    // TODO optimize this shit. maybe remove amt
    @Override
    public void highlightLand(Chunk chunk, Player p, Particle e, int amt) {
        this.pl.getWGProxy().highlightLand(chunk, p, e, amt);
    }

    /**
     * Gets a location in a chunk on one of the corners. Its the highest possible location.
     * Also loads the chunk
     *
     * @return returns a location on top of the chunk
     */
    @Override
    public Location getALocation() {
        IWorldGuardProxy wg = pl.getWGProxy();
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
        IWorldGuardProxy wg = pl.getWGProxy();
        World w = wg.getWorld(getName());
        int x = wg.getX(getName());
        int z = wg.getZ(getName());

        if (w != null && x != Integer.MIN_VALUE && z != Integer.MIN_VALUE) {
            return w.getChunkAt(x, z);
        }
        return null;
    }

}
