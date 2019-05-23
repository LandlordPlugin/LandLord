package biz.princeps.landlord.api;

import org.bukkit.entity.Player;

public interface IMapManager {

    /**
     * Toggles the land map for a specific player.
     * If it was on, it will be off now, and the other way around.
     *
     * @param p the player
     */
    void toggleMap(Player p);

    /**
     * Adds a land map for a player.
     * Doesnt do anything, if the player already has a map.
     *
     * @param p the player
     */
    void addMap(Player p);

    /**
     * Removes a landmap for a player.
     * Doesnt do anything, if the player has no map.
     *
     * @param p the player
     */
    void removeMap(Player p);

    /**
     * Removes all maps, that are currently displayed to players.
     */
    void removeAllMaps();

    /**
     * Updates all maps, that are currently displayed to players.
     */
    void updateAll();

    /**
     * Updates a map for a specific player
     *
     * @param player the player
     */
    void update(String player);

    /**
     * Checks, if a player has a landmap opened.
     *
     * @param playername the player
     * @return if the player has a landmap openf
     */
    boolean hasMap(String playername);
}
