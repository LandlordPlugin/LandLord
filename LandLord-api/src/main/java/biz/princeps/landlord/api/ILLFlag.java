package biz.princeps.landlord.api;

import org.bukkit.Material;

/**
 * A land contains a set of ILLFlags.
 * These class represents a toggleable stateflag.
 * It has basically 3 modis:
 * - Allow all
 * - Friends only
 * - Owner only
 * There is one invalid state: everyone but friends.
 * When you try to toggle to that state with the methods below it wont work.
 */
public interface ILLFlag {

    /**
     * The name of the flag.
     * Examples: farewell, greeting, build, interact, ...
     *
     * @return the flag name.
     */
    String getName();

    /**
     * Toggles the friends if possible.
     *
     * @return if the toggle was successful
     */
    boolean toggleFriends();

    /**
     * Toggles "everyone" if possible.
     *
     * @return if the toggle was successful
     */
    boolean toggleAll();

    /**
     * Gets the friend status as a boolean.
     * true = friends are allowed.
     * false = friends are denied.
     *
     * @return friend status
     */
    boolean getFriendStatus();

    /**
     * Gets the everyone status as a boolean.
     * true = everyone is allowed.
     * false = everyone are denied.
     *
     * @return everyone status
     */
    boolean getAllStatus();

    /**
     * Gets the material, that represents this flag in the manage gui.
     */
    Material getMaterial();
}
