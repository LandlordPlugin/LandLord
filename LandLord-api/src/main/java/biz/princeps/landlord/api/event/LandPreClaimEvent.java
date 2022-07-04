package biz.princeps.landlord.api.event;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 * <p>
 * This event is called, before the actual claiming process starts.
 * You can still cancel the process at this point by cancelling this event.
 */
public class LandPreClaimEvent implements LandLordEvent {

    private boolean cancelled;

    private final Player player;
    private final Chunk land;

    public LandPreClaimEvent(Player player, Chunk land) {
        this.player = player;
        this.land = land;
    }

    /**
     * Returns the player, who wants to claim a land
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the chunk, which should be claimed
     */
    public Chunk getChunk() {
        return land;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}



