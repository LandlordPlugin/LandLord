package biz.princeps.landlord.api.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: ?
 * <p>
 * <p>
 * Called whenever a player modifies the wilderness. This includes, but is not limited to breaking/placing blocks in
 * unclaimed regions.
 */
public class PlayerBrokeSecureWorldEvent implements LandLordEvent {

    private boolean isCancelled;

    private final Player player;
    private final Block block;
    private final Cancellable cancellable;

    public PlayerBrokeSecureWorldEvent(Player player, Block block, Cancellable cancellable) {
        this.player = player;
        this.block = block;
        this.cancellable = cancellable;
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

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
