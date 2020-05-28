package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMapManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MapListener extends BasicListener {

    private final IMapManager mapManager;

    public MapListener(ILandLord pl) {
        super(pl);
        this.mapManager = pl.getMapManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLeave(PlayerQuitEvent event) {
        mapManager.removeMap(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerWorldChange(PlayerChangedWorldEvent event) {
        mapManager.removeMap(event.getPlayer());
    }

    /**
     * Update map when player teleports
     *
     * @param event that was triggered
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerTeleportKeepMap(PlayerTeleportEvent event) {
        handleMapRefresh(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerRespawn(PlayerRespawnEvent event) {
        handleMapRefresh(event.getPlayer());
    }

    private void handleMapRefresh(Player player) {
        final Player p = player;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin.getPlugin(), () -> mapManager.update(p.getUniqueId()), 15L);
    }

}

