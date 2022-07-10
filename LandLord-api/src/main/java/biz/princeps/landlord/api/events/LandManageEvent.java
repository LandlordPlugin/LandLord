package biz.princeps.landlord.api.events;

import biz.princeps.landlord.api.IOwnedLand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: ?
 * <p>
 * <p>
 * Called whenever a land ist being managed (on click in the gui). Doesnt trigger for setfarewell like flags.
 */
public class LandManageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final IOwnedLand land;
    private final String flagChanged;
    private final Object oldValue;
    private final Object newValue;


    public LandManageEvent(Player player, IOwnedLand land, String flagChanged, Object oldValue, Object newValue) {
        super(true);
        this.player = player;
        this.land = land;
        this.flagChanged = flagChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
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
     * Returns the bought land
     */
    public IOwnedLand getLand() {
        return land;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getFlagChanged() {
        return flagChanged;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
