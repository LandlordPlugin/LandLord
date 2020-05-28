package biz.princeps.landlord.api.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: ?
 * <p>
 * <p>
 * Called whenever a player modifies the wilderness. This includes, but is not limited to breaking/placing blocks in
 * unclaimed regions.
 */
public class PlayerBrokeSecureWorldEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled;

    private final Player player;
    private final Block block;
    private final Cancellable cancellable;

    public PlayerBrokeSecureWorldEvent(Player player, Block block, Cancellable cancellable) {
        this.player = player;
        this.block = block;
        this.cancellable = cancellable;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the player, who broke land he is not allowed to break yet
     */
    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return block.getLocation();
    }

    public Block getBlock() {
        return block;
    }

    public void allow() {
        this.cancellable.setCancelled(false);
    }

    public void deny() {
        this.cancellable.setCancelled(true);
    }

    public void setSecureState(boolean bool) {
        this.cancellable.setCancelled(bool);
    }

    public Cancellable getCancellable() {
        return cancellable;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
