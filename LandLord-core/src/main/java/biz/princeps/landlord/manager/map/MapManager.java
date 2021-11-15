package biz.princeps.landlord.manager.map;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMapManager;
import biz.princeps.landlord.util.MapConstants;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * File created by jcdesimp on 3/10/14. updated by SpatiumPrinceps on 19/07/17
 */
public class MapManager implements IMapManager {

    private final HashMap<UUID, LandMap> mapList;
    private final ILandLord plugin;
    private final MapConstants constants;

    public MapManager(ILandLord plugin) {
        super();
        this.plugin = plugin;
        this.mapList = new HashMap<>();
        this.constants = new MapConstants(plugin.getConfig());
    }

    @Override
    public void toggleMap(Player p) {
        if (hasMap(p.getUniqueId())) {
            removeMap(p);
        } else {
            addMap(p);
        }
    }

    @Override
    public void addMap(Player player) {
        if (!hasMap(player.getUniqueId())) {
            mapList.put(player.getUniqueId(), new LandMap(player, plugin, constants));
        }
    }

    @Override
    public void removeMap(Player player) {
        UUID pUUID = player.getUniqueId();
        if (hasMap(pUUID)) {
            LandMap curr = mapList.get(pUUID);
            curr.removeMap();
            mapList.remove(pUUID);
        }
    }

    @Override
    public void removeAllMaps() {
        for (UUID uuid : mapList.keySet()) {
            mapList.get(uuid).removeMap();
        }
        mapList.clear();
    }

    @Override
    public void updateAll() {
        for (UUID uuid : mapList.keySet()) {
            mapList.get(uuid).forceUpdate();
        }
    }

    @Override
    public void update(UUID playerUUID) {
        if (hasMap(playerUUID)) {
            mapList.get(playerUUID).forceUpdate();
        }
    }

    @Override
    public boolean hasMap(UUID playerUUID) {
        return mapList.containsKey(playerUUID);
    }
}