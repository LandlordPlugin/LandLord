package biz.princeps.landlord.api;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public interface IDelimitationManager {

    /**
     * Delimits a chunk for a player if enabled.
     * Takes a string list (CommandSettings.Claim.delimitation) as a pattern.
     * Also the delimitation may be phantomblocks only.
     *
     * @param player the player to delimit the chunk for
     * @param chunk  the chunk to delimit
     */
    void delimit(Player player, Chunk chunk);
}
