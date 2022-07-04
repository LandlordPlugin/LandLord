package biz.princeps.landlord.api.event;

import biz.princeps.landlord.api.IOwnedLand;
import org.bukkit.command.CommandSender;

/**
 * This event is called when a land is cleared.
 */
public class LandClearEvent implements LandLordEvent {

    private boolean cancelled;

    private final CommandSender initiator;
    private final IOwnedLand land;

    public LandClearEvent(CommandSender initiator, IOwnedLand land) {
        this.initiator = initiator;
        this.land = land;
    }

    public CommandSender getInitiator() {
        return initiator;
    }

    public IOwnedLand getLand() {
        return land;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

}
