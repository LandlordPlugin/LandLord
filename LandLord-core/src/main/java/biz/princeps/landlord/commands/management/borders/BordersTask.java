package biz.princeps.landlord.commands.management.borders;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BordersTask extends BukkitRunnable {

    private final ILandLord plugin;
    private final IWorldGuardManager wg;
    private final Player player;

    private final int refreshRate;
    private final int timeout;

    private int counter;

    BordersTask(ILandLord plugin, Player player) {
        this.plugin = plugin;
        this.wg = plugin.getWGManager();
        this.player = player;

        this.refreshRate = plugin.getConfig().getInt("Borders.refreshRate");
        this.timeout = plugin.getConfig().getInt("Borders.timeout");
    }

    @Override
    public void run() {
        if (counter * refreshRate <= timeout) {
            if (plugin.getConfig().getBoolean("Particles.borders.enabled")) {
                IOwnedLand ownedLand = wg.getRegion(player.getLocation());

                if (ownedLand == null) {
                    wg.highlightLand(player.getLocation().getChunk(), player,
                            Particle.valueOf(plugin.getConfig().getString("Particles.borders.unclaimed")), 1, false);
                } else {
                    plugin.getPlayerManager().getOffline(ownedLand.getOwner(), (owner) -> {
                        if (plugin.getPlayerManager().isInactive(owner.getLastSeen())) {
                            wg.highlightLand(player.getLocation().getChunk(), player,
                                    Particle.valueOf(plugin.getConfig().getString("Particles.borders.inactive")), 1, false);
                        } else {
                            wg.highlightLand(player.getLocation().getChunk(), player,
                                    Particle.valueOf(plugin.getConfig().getString("Particles.borders.claimed")), 1, false);
                        }
                    });
                }
            }
        } else {
            cancel();
        }
        counter++;
    }

    public void start() {
        runTaskTimer(plugin.getPlugin(), 0, refreshRate * 20L);
    }

}
