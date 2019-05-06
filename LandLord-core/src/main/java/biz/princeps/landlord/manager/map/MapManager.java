package biz.princeps.landlord.manager.map;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.listener.BasicListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

/**
 * File created by jcdesimp on 3/10/14. updated by SpatiumPrinceps on 19/07/17
 */
public class MapManager extends BasicListener {

    private HashMap<String, LandMap> mapList;

    public MapManager() {
        super();
        this.mapList = new HashMap<>();
    }

    public void toggleMap(Player p) {
        if (mapList.containsKey(p.getName())) {
            remMap(p);
        } else {
            addMap(p);
        }
    }

    public void addMap(Player player) {
        if (!mapList.containsKey(player.getName())) {
            mapList.put(player.getName(), new LandMap(player, this.plugin));
        }
    }

    public void remMap(Player player) {
        String pName = player.getName();
        if (mapList.containsKey(pName)) {
            LandMap curr = mapList.get(pName);
            curr.removeMap();
            mapList.remove(pName);
        }
    }

    public void removeAllMaps() {
        for (String k : mapList.keySet()) {
            mapList.get(k).removeMap();
        }
        mapList.clear();
    }

    public void updateAll() {
        for (String k : mapList.keySet()) {
            mapList.get(k).forceUpdate();
        }
    }


    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        if (mapList.containsKey(event.getPlayer().getName())) {
            remMap(event.getPlayer());
        }

    }

    @EventHandler
    public void playerWorldChange(PlayerChangedWorldEvent event) {
        if (mapList.containsKey(event.getPlayer().getName())) {
            remMap(event.getPlayer());
        }
    }


    /**
     * Update map when player teleports
     * TODO make this duplicate code pretty
     * @param event that was triggered
     */
    @EventHandler
    public void playerTeleportKeepMap(PlayerTeleportEvent event) {
        final Player p = event.getPlayer();

        if (mapList.containsKey(event.getPlayer().getName())) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin.getPluginInstance(), () -> {
                if (mapList.containsKey(p.getName())) {
                    mapList.get(p.getName()).forceUpdate();
                }
            }, 15L);
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        final Player p = event.getPlayer();
        if (mapList.containsKey(event.getPlayer().getName())) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin.getPluginInstance(), () -> {
                if (mapList.containsKey(p.getName())) {
                    mapList.get(p.getName()).forceUpdate();
                }
            }, 15L);
        }
    }


}