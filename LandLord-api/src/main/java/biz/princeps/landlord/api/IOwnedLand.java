package biz.princeps.landlord.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public interface IOwnedLand {

    /**
     * Gets the name of a land.
     * Format: worldname_xchunk_zchunk
     *
     * @return the land name
     */
    String getName();

    /**
     * Gets a nicely formatted string of the owner.
     * Example: "SpatiumPrinceps" instead of the uuid.
     *
     * @return a string of the owner
     */
    String getOwnersString();

    /**
     * Gets a nicely formatted string of the friends.
     * Example: "SpatiumPrinceps, Aurelien"
     *
     * @return a string of the members
     */
    String getMembersString();

    /**
     * Check if a players uuid is owner
     *
     * @param uuid the uuid
     * @return if the uuid is owner
     */
    boolean isOwner(UUID uuid);

    /**
     * Gets the uuid of the owner of the land
     *
     * @return the owner
     */
    UUID getOwner();

    /**
     * Replaces the current owner with the provided uuid
     *
     * @param uuid the new uuid
     */
    void replaceOwner(UUID uuid);

    boolean isFriend(UUID uuid);

    Set<UUID> getFriends();

    void addFriend(UUID uuid);

    void removeFriend(UUID uuid);

    World getWorld();

    Chunk getChunk();

    /**
     * Highlights the border around the chunk the player is standing in with a particle effect.
     *
     * @param p     player
     * @param chunk the chunk to highlight
     * @param pa    the particle effects to use
     */
    void highlightLand(Chunk chunk, Player p, Particle pa);

    /**
     * Highlight a specific chunk for a player with particles
     *
     * @param chunk the chunk
     * @param p     the player to highlight the chunk for
     * @param e     the particles
     * @param amt   amount of particles
     */
    void highlightLand(Chunk chunk, Player p, Particle e, int amt);

    /**
     * Highlights the border around the chunk the player is standing in with a particle effect.
     *
     * @param p player
     * @param e effect to play
     */
    void highlightLand(Player p, Particle e);

    /**
     * Get a location, thats within the land. Its the highest y location in one of the corners.
     *
     * @return a location inside the chunk
     */
    Location getALocation();

    boolean contains(int x, int y, int z);

    List<ILLFlag> getFlags();

    String getGreetMessage();

    void setGreetMessage(String newmsg);

    String getFarewellMessage();

    void setFarewellMessage(String newmsg);

    /**
     * Toggles the worldguard mob-spawn deny flag for a mob
     *
     * @param mob the mob
     */
    void toggleMob(IMob mob);

    /**
     * Checks if a mob is denied by the worldguard mob-spawn flag.
     *
     * @param mob the mob
     * @return if the mob is denied
     */
    boolean isMobDenied(IMob mob);

    /**
     * Gets a price for a land. -1 if the land is not for sale.
     *
     * @return the price
     */
    double getPrice();

    /**
     * Sets the price for a land. If its greater then -1, other players are able to buy up this land.
     *
     * @param price the price
     */
    void setPrice(double price);

    /**
     * Init default flags for a land.
     *
     * @param owner the land's owner
     */
    void initFlags(UUID owner);

    /**
     * Update flags for a land (add missing flags and remove non existing flags).
     *
     * @param owner the land's owner
     */
    void updateFlags(UUID owner);

    /**
     * Set the priority of a land region (incidence when it is not zero)
     *
     */
    void initRegionPriority();
}
