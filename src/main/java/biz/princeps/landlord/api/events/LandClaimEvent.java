package biz.princeps.landlord.api.events;

import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandClaimEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private OwnedLand land;
    private ClaimState claimState;

    public LandClaimEvent(Player player, OwnedLand land, ClaimState claimState) {
        this.player = player;
        this.land = land;
        this.claimState = claimState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public OwnedLand getLand() {
        return land;
    }

    public ClaimState getClaimState() {
        return claimState;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }


    public enum ClaimState {
        SUCCESS,
        NOTENOUGHMONEY,
        OVERLAPPINGREGION,
        ALREADYCLAIMED
    }

}



