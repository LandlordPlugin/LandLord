package biz.princeps.landlord.api.events;

import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandUnclaimEvent extends Event implements Cancellable {

    private HandlerList list = this.getHandlers();
    private boolean cancelled;

    private Player player;
    private OwnedLand land;

    public LandUnclaimEvent(Player player, OwnedLand land) {
        this.list = this.getHandlers();
        this.player = player;
        this.land = land;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

}



