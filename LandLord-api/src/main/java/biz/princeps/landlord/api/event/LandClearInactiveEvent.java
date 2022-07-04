package biz.princeps.landlord.api.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * This event is called when a player is cleared due to its inactivity.
 */
public class LandClearInactiveEvent implements LandLordEvent {

    private boolean cancelled;

    private final CommandSender initiator;
    private final OfflinePlayer clearedPlayer;

    public LandClearInactiveEvent(CommandSender initiator, OfflinePlayer clearedPlayer) {
        this.initiator = initiator;
        this.clearedPlayer = clearedPlayer;
    }

    public CommandSender getInitiator() {
        return initiator;
    }

    public OfflinePlayer getClearedPlayer() {
        return clearedPlayer;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

}
