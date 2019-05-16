package biz.princeps.landlord.api;

import org.bukkit.entity.Player;

public interface IMapManager {

    void toggleMap(Player p);

    void addMap(Player p);

    void removeMap(Player p);

    void removeAllMaps();

    void updateAll();

    void update(String player);

    boolean hasMap(String playername);
}
