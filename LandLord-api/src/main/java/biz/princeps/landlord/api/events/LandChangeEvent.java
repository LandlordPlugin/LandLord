package biz.princeps.landlord.api.events;

import biz.princeps.landlord.api.IOwnedLand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * This event is fired when a player changes the land.
 */
public class LandChangeEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final IOwnedLand previousLand;
    private final IOwnedLand newLand;

    public LandChangeEvent(Player who, IOwnedLand previousLand, IOwnedLand newLand) {
        super(who);
        this.previousLand = previousLand;
        this.newLand = newLand;
    }

    /**
     * Returns the land the player previously was in. Might be {@code null} if and only if
     * the player wasn't in a land before (and entered a land). Therefore, {@link #getNewLand()} wont
     * be {@code null} at the same time.
     *
     * @return the land the player previously was in, or null.
     */
    public IOwnedLand getPreviousLand() {
        return previousLand;
    }

    /**
     * Returns the land the player went into. Might be {@code null} if and only if
     * the player left a land. Therefore, {@link #getPreviousLand()} wont be {@code null}
     * at the same time.
     *
     * @return the land the player entered, or null.
     */
    public IOwnedLand getNewLand() {
        return newLand;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
