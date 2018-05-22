package biz.princeps.landlord.listener;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.events.FinishedLoadingPlayerEvent;
import biz.princeps.landlord.persistent.LPlayer;
import co.aikar.taskchain.TaskChain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 */
public class JoinListener extends BasicListener {

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        Player p = event.getPlayer();

        TaskChain<Object> chain = Landlord.newChain();
        chain.asyncFirst(() -> plugin.getPlayerManager().getOfflinePlayerSync(p.getUniqueId()))
                .abortIfNull()
                .storeAsData("lp")
                .sync(() -> {
                    LPlayer lPlayer = chain.getTaskData("lp");
                    if (lPlayer == null) {
                        lPlayer = new LPlayer(p.getUniqueId());
                    }
                    plugin.getPlayerManager().add(lPlayer);

                    // The next to lines are needed to protect claiming of "inactive" lands although the owner is online right now
                    // might just be a rare never happening edge case, but lets be safe
                    lPlayer.setName(p.getName());
                    lPlayer.setLastSeen(LocalDateTime.now());
                })
                .async(() -> {
                    plugin.getPlayerManager().saveSync(p.getUniqueId());

                    Event e = new FinishedLoadingPlayerEvent(p, chain.getTaskData("lp"));
                    Bukkit.getPluginManager().callEvent(e);
                });
        chain.execute();
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        plugin.getPlayerManager().get(p.getUniqueId()).setLastSeen(LocalDateTime.now());
        plugin.getPlayerManager().saveAsync(p.getUniqueId());
        plugin.getPlayerManager().remove(p.getUniqueId());
    }
}
