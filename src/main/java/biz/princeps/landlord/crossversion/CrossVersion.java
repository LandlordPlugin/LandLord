package biz.princeps.landlord.crossversion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 28.07.17.
 */
public class CrossVersion {

    private IActionBar bar;
    private ISpawnParticle particle;

    public CrossVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

        switch (version) {

            case "v1_8_R3":
                bar = new biz.princeps.landlord.crossversion.v1_8_R3.ActionBar();
                particle = new biz.princeps.landlord.crossversion.v1_8_R3.SpawnParticle();
                break;

            case "v1_9_R2":
                bar = new biz.princeps.landlord.crossversion.v1_9_R2.ActionBar();
                particle = new biz.princeps.landlord.crossversion.v1_9_R2.SpawnParticle();
                break;

            case "v1_10_R1":
                bar = new biz.princeps.landlord.crossversion.v1_10_R1.ActionBar();
                particle = new biz.princeps.landlord.crossversion.v1_10_R1.SpawnParticle();
                break;

            case "v1_11_R1":
                bar = new biz.princeps.landlord.crossversion.v1_11_R1.ActionBar();
                particle = new biz.princeps.landlord.crossversion.v1_11_R1.SpawnParticle();
                break;

            case "v1_12_R1":
                bar = new biz.princeps.landlord.crossversion.v1_12_R1.ActionBar();
                particle = new biz.princeps.landlord.crossversion.v1_12_R1.SpawnParticle();
                break;

            default:
                bar = new biz.princeps.landlord.crossversion.v1_12_R1.ActionBar();
                particle = new biz.princeps.landlord.crossversion.v1_12_R1.SpawnParticle();
        }

    }

    /**
     * just in case I ever want to upgrade this to a proper interface based system
     *
     * @param player
     * @param msg
     */
    public void sendActionBar(Player player, String msg) {
        bar.sendActionBar(player, msg);
    }

    public void spawnParticle(Location loc, CParticle cParticle, int amt) {
        particle.spawnParticle(loc, cParticle, amt);
    }
}
