package biz.princeps.landlord.api.event;

import biz.princeps.landlord.api.IOwnedLand;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 * <p>
 * This event is called when a land is unclaimed
 */
public class LandUnclaimEvent implements LandLordEvent {

    private boolean cancelled;

    private final Player player;
    private final IOwnedLand land;

    public LandUnclaimEvent(Player player, IOwnedLand land) {
        this.player = player;
        this.land = land;
    }

    public Player getPlayer() {
        return player;
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



