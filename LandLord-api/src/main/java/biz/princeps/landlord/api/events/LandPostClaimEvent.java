package biz.princeps.landlord.api.events;

import biz.princeps.landlord.api.ClaimType;
import biz.princeps.landlord.api.IOwnedLand;
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
    private IOwnedLand land;
    private ClaimType type;

    public LandPostClaimEvent(Player player, IOwnedLand land, ClaimType type) {
        this.player = player;
        this.land = land;
        this.type = type;
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

    public ClaimType getClaimType(){
        return type;
    }

}
