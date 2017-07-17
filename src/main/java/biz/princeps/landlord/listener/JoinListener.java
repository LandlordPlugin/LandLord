package biz.princeps.landlord.listener;

import biz.princeps.landlord.persistent.LPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by spatium on 17.07.17.
 */
public class JoinListener extends BasicListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        Map<String, Object> condis = new HashMap<>();
        condis.put("uuid", p.getUniqueId().toString());
        List<Object> lPlayer = plugin.getDatabaseAPI().retrieveObjects(LPlayer.class, condis);
        LPlayer lp = (LPlayer) lPlayer.get(0);
        if (lp == null)
            lp = new LPlayer(p.getUniqueId());

        plugin.getPlayerManager().add(p.getUniqueId(), lp);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {

        LPlayer lp = plugin.getPlayerManager().get(event.getPlayer().getUniqueId());
        plugin.getDatabaseAPI().saveObject(lp);

        plugin.getPlayerManager().remove(lp.getUuid());
    }
}
