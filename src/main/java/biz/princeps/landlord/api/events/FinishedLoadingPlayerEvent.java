package biz.princeps.landlord.api.events;

import biz.princeps.landlord.persistent.LPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 1/21/18
 * <p>
 * <p>
 * Called when a player joins the server and the async loading of the LPlayer is finished.
 * Be careful, its ASYNC!
 */
public class FinishedLoadingPlayerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private LPlayer lPlayer;

    public FinishedLoadingPlayerEvent(Player player, LPlayer lPlayer) {
        this.player = player;
        this.lPlayer = lPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public LPlayer getLPlayer() {
        return lPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
