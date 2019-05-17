package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IPossessedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class APossessedLand extends ALand implements IPossessedLand {

    protected ILandLord pl;
    protected Set<String> chunks;

    public APossessedLand(ILandLord pl, World world, String name) {
        super(world, name);
        this.pl = pl;
    }

    @Override
    public void addLand(IPossessedLand land) {
        chunks.add(land.getName());
    }

    @Override
    public void removeLand(String name) {
        chunks.remove(name);
    }

    @Override
    public Set<String> getAllLandNames() {
        return chunks;
    }
}
