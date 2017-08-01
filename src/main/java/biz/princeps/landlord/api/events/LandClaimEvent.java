package biz.princeps.landlord.api.events;

import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandClaimEvent extends Event implements Cancellable {

    private HandlerList list = this.getHandlers();
    private boolean cancelled;

    private Player player;
    private OwnedLand land;
    private ClaimState claimState;

    public LandClaimEvent(Player player, OwnedLand land, ClaimState claimState) {
        this.list = this.getHandlers();
        this.player = player;
        this.land = land;
        this.claimState = claimState;
    }

    @Override
    public HandlerList getHandlers() {
        return list;
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
        ALREADYCLAIMED
    }

}



