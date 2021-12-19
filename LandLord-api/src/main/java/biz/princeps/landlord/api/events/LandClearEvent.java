package biz.princeps.landlord.api.events;

import biz.princeps.landlord.api.IOwnedLand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 * This event is called when a land is cleared.
 */
public class LandClearEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final CommandSender initiator;
    private final IOwnedLand land;

    public LandClearEvent(CommandSender initiator, IOwnedLand land) {
        this.initiator = initiator;
        this.land = land;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public CommandSender getInitiator() {
        return initiator;
    }

    public IOwnedLand getLand() {
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
