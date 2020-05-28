package biz.princeps.landlord.api.events;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 * <p>
 * This event is called, before the actual claiming process starts.
 * You can still cancel the process at this point by cancelling this event.
 */
public class LandPreClaimEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private final Chunk land;

    public LandPreClaimEvent(Player player, Chunk land) {
        this.player = player;
        this.land = land;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


}



