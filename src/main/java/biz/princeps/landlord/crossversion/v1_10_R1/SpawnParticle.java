package biz.princeps.landlord.crossversion.v1_10_R1;

import biz.princeps.landlord.crossversion.CParticle;
import biz.princeps.landlord.crossversion.ISpawnParticle;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Created by spatium on 28.07.17.
 */
public class SpawnParticle implements ISpawnParticle {


    @Override
    public void spawnParticle(Location loc, CParticle particle, int amount) {
        loc.getWorld().spawnParticle(Particle.valueOf(particle.getV19()), loc, amount);
    }
}
