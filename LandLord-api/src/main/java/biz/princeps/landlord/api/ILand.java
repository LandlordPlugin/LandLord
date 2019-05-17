package biz.princeps.landlord.api;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public interface ILand {
    String getName();

    World getWorld();

    void highlight(Player p, Particle pa);

    void highlight(Player p, Particle e, int amt);

    Location getALocation();

    boolean contains(int x, int y, int z);
}
