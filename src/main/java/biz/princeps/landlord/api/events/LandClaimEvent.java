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
    private ClaimState claimState;
    private int x, z;

    public LandClaimEvent(Player player, int x, int z, ClaimState claimState) {
        this.player = player;
        this.x = x;
        this.z = z;
        this.claimState = claimState;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


    public enum ClaimState {
        SUCCESS,
        NOTENOUGHMONEY,
        OVERLAPPINGREGION,
        ALREADYCLAIMED
    }

}



