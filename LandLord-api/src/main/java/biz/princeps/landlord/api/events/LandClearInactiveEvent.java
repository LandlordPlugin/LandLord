package biz.princeps.landlord.api.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 * This event is called when a player is cleared due to its inactivity.
 */
public class LandClearInactiveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final CommandSender initiator;
    private final OfflinePlayer clearedPlayer;

    public LandClearInactiveEvent(CommandSender initiator, OfflinePlayer clearedPlayer) {
        this.initiator = initiator;
        this.clearedPlayer = clearedPlayer;
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

    public OfflinePlayer getClearedPlayer() {
        return clearedPlayer;
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


