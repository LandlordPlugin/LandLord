package biz.princeps.landlord.api.events;

import biz.princeps.landlord.api.IPossessedLand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 12/5/17
 * <p>
 * This event is called, as soon as the land claim process is finished and the player actually owns the land
 */
public class LandPostClaimEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private IPossessedLand land;

    public LandPostClaimEvent(Player player, IPossessedLand land) {
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
     * Returns the bought land
     */
    public IPossessedLand getLand() {
        return land;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


}
