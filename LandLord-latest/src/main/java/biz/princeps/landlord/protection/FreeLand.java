package biz.princeps.landlord.protection;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FreeLand extends AFreeLand {

    public FreeLand(World world, String name) {
        super(world, name);
    }

    @Override
    public void highlight(Player p, Particle pa) {

    }

    @Override
    public void highlight(Player p, Particle e, int amt) {

    }

    @Override
    public Location getALocation() {
        return null;
    }

    @Override
    public boolean contains(int x, int y, int z) {
        return false;
    }

    @Override
    public void claim(UUID id) {

    }
}
