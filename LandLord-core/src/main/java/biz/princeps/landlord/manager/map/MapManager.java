package biz.princeps.landlord.manager.map;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMapManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * File created by jcdesimp on 3/10/14. updated by SpatiumPrinceps on 19/07/17
 */
public class MapManager implements IMapManager {

    private HashMap<String, LandMap> mapList;
    private ILandLord pl;

    public MapManager(ILandLord pl) {
        super();
        this.pl = pl;
        this.mapList = new HashMap<>();
    }

    @Override
    public void toggleMap(Player p) {
        if (mapList.containsKey(p.getName())) {
            removeMap(p);
        } else {
            addMap(p);
        }
    }

    @Override
    public void addMap(Player player) {
        if (!mapList.containsKey(player.getName())) {
            mapList.put(player.getName(), new LandMap(player, pl));
        }
    }

    @Override
    public void removeMap(Player player) {
        String pName = player.getName();
        if (mapList.containsKey(pName)) {
            LandMap curr = mapList.get(pName);
            curr.removeMap();
            mapList.remove(pName);
        }
    }

    @Override
    public void removeAllMaps() {
        for (String k : mapList.keySet()) {
            mapList.get(k).removeMap();
        }
        mapList.clear();
    }

    @Override
    public void updateAll() {
        for (String k : mapList.keySet()) {
            mapList.get(k).forceUpdate();
        }
    }

    @Override
    public void update(String player) {
        if (hasMap(player)) {
            mapList.get(player).forceUpdate();
        }
    }

    @Override
    public boolean hasMap(String playername) {
        return mapList.containsKey(playername);
    }
}