package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMapManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MapListener extends BasicListener {

    private IMapManager mapManager;

    public MapListener(ILandLord pl) {
        super(pl);
        this.mapManager = pl.getMapManager();
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        if (mapManager.hasMap(event.getPlayer().getName())) {
            mapManager.removeMap(event.getPlayer());
        }

    }

    @EventHandler
    public void playerWorldChange(PlayerChangedWorldEvent event) {
        if (mapManager.hasMap(event.getPlayer().getName())) {
            mapManager.removeMap(event.getPlayer());
        }
    }

    /**
     * Update map when player teleports
     *
     * @param event that was triggered
     */
    @EventHandler
    public void playerTeleportKeepMap(PlayerTeleportEvent event) {
        handleMapRefresh(event.getPlayer());
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        handleMapRefresh(event.getPlayer());
    }

    private void handleMapRefresh(Player player) {
        final Player p = player;
        if (mapManager.hasMap(player.getName())) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin.getPlugin(), () -> {
                if (mapManager.hasMap(p.getName())) {
                    mapManager.update(p.getName());
                }
            }, 15L);
        }
    }

}

