package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardProxy;
import biz.princeps.lib.PrincepsLib;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class AOwnedLand implements IOwnedLand {

    protected World world;

    public void highlightLand(Chunk chunk, Player p, Particle pa) {
        highlightLand(chunk, p, pa, 5);
    }

    /**
     * Highlights the border around the chunk with a particle effect.
     *
     * @param p player
     * @param e effect to play
     */
    public void highlightLand(Player p, Particle e) {
        highlightLand(p.getLocation().getChunk(), p, e, 5);
    }

    // TODO optimize this shit.
    public void highlightLand(Chunk chunk, Player p, Particle e, int amt) {
        if (!Landlord.getInstance().getConfig().getBoolean("options.particleEffects", true)) {
            return;
        }
        List<Location> edgeBlocks = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            for (int ii = -1; ii <= 10; ii++) {
                edgeBlocks.add(chunk.getBlock(i, (int) (p.getLocation().getY()) + ii, 15).getLocation());
                edgeBlocks.add(chunk.getBlock(i, (int) (p.getLocation().getY()) + ii, 0).getLocation());
                edgeBlocks.add(chunk.getBlock(0, (int) (p.getLocation().getY()) + ii, i).getLocation());
                edgeBlocks.add(chunk.getBlock(15, (int) (p.getLocation().getY()) + ii, i).getLocation());
            }
        }
        for (Location edgeBlock : edgeBlocks) {
            edgeBlock.setZ(edgeBlock.getBlockZ() + .5);
            edgeBlock.setX(edgeBlock.getBlockX() + .5);
            PrincepsLib.getStuffManager().spawnParticle(edgeBlock, e, amt);
        }
    }

    /**
     * also loads a chunk
     *
     * @return returns a location on top of the chunk
     */
    public Location getALocation() {
        IWorldGuardProxy wg = Landlord.getInstance().getWgproxy();
        World world = wg.getWorld(getName());
        if (world == null)
            return null;

        int x = wg.getX(getName());
        int z = wg.getZ(getName());
        return new Location(world, x * 16, world.getHighestBlockYAt(x * 16, z * 16) + 1, z * 16);

    }
}
