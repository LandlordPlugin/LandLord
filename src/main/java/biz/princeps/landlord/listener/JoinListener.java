package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.events.FinishedLoadingPlayerEvent;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.lib.storage_old.requests.Conditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 */
public class JoinListener extends BasicListener {

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        Player p = event.getPlayer();

        new BukkitRunnable() {

            @Override
            public void run() {
                List<Object> lPlayer = plugin.getDatabaseAPI().retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("uuid", p.getUniqueId().toString()).create());
                LPlayer lp;
                if (lPlayer.size() > 0)
                    lp = (LPlayer) lPlayer.get(0);
                else
                    lp = new LPlayer(p.getUniqueId());

                if (lp.getName() == null || lp.getName().isEmpty() || !p.getName().equals(lp.getName())) {
                    lp.setName(p.getName());
                }

                plugin.getPlayerManager().add(p.getUniqueId(), lp);

                // The next to lines are needed to protect claiming of "inactive" lands although the owner is online right now
                // might just be a rare never happening edge case, but lets be safe
                plugin.getPlayerManager().get(p.getUniqueId()).setLastSeen(LocalDateTime.now());
                plugin.getPlayerManager().save(p.getUniqueId());

                Event event = new FinishedLoadingPlayerEvent(p, lp);
                Bukkit.getPluginManager().callEvent(event);
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        new BukkitRunnable() {

            @Override
            public void run() {
                plugin.getPlayerManager().get(p.getUniqueId()).setLastSeen(LocalDateTime.now());
                plugin.getPlayerManager().save(p.getUniqueId());
                plugin.getPlayerManager().remove(p.getUniqueId());
            }
        }.runTaskAsynchronously(plugin);
    }
}
