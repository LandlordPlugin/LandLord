package biz.princeps.landlord.listener;

import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.lib.storage.requests.Conditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by spatium on 17.07.17.
 */
public class JoinListener extends BasicListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
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

                plugin.getPlayerManager().add(p.getUniqueId(), lp);
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
