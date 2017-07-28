package biz.princeps.landlord.crossversion;

import org.bukkit.Location;
/**
 * Created by spatium on 28.07.17.
 */
public interface ISpawnParticle {


    void spawnParticle(Location loc, CParticle particle, int amount);
}
