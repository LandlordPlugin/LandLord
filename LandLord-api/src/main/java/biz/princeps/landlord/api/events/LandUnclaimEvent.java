package biz.princeps.landlord.api.events;

import biz.princeps.landlord.api.IPossessedLand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 * <p>
 * This event is called when a land is unclaimed
 */
public class LandUnclaimEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private IPossessedLand land;

    public LandUnclaimEvent(Player player, IPossessedLand land) {
        this.player = player;
        this.land = land;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public IPossessedLand getLand() {
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

}



