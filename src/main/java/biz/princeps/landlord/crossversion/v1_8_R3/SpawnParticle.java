package biz.princeps.landlord.crossversion.v1_8_R3;

import biz.princeps.landlord.crossversion.CParticle;
import biz.princeps.landlord.crossversion.ISpawnParticle;
import org.bukkit.Effect;
import org.bukkit.Location;

/**
 * Created by spatium on 28.07.17.
 */
public class SpawnParticle implements ISpawnParticle {


    @Override
    public void spawnParticle(Location loc, CParticle particle, int amount) {
        loc.getWorld().playEffect(loc, Effect.valueOf(particle.getV18()), amount);
    }
}
