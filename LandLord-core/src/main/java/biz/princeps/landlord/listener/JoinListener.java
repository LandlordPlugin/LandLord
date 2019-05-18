package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.persistent.LPlayer;
import org.bukkit.entity.Player;
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

    public JoinListener(ILandLord plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        IPlayer offline = plugin.getPlayerManager().getOffline(p.getUniqueId());

        if (offline == null) {
            offline = new LPlayer(p.getUniqueId());
        }
        plugin.getPlayerManager().add(offline);

        // The next to lines are needed to protect claiming of "inactive" lands although the owner is
        // online right now
        // might just be a rare never happening edge case, but lets be safe
        offline.setLastSeen(LocalDateTime.now());

        plugin.getPlayerManager().save(offline, true);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        IPlayer lp = plugin.getPlayerManager().get(p.getUniqueId());
        if (lp == null) {
            return;
        }
        lp.setLastSeen(LocalDateTime.now());
        plugin.getPlayerManager().save(lp, true);
        plugin.getPlayerManager().remove(p.getUniqueId());
    }
}
