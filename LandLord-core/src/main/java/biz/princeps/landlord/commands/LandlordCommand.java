package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.util.JavaUtils;
import biz.princeps.lib.command.SubCommand;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/7/17
 * <p>
 * Every landlord command should extend this class.
 * Actually, this is not required, but advised, since you dont have to get the instances for pluging and LangManager yourself.
 * In addition, there's a disabled world check, which comes in handy.
 * <p>
 * Major command logic is done in Landlordbase
 */
public abstract class LandlordCommand extends SubCommand {

    protected final ILandLord plugin;
    protected final ILangManager lm;

    public LandlordCommand(ILandLord plugin, String name, String usage, Set<String> permissions, Set<String> aliases) {
        super(name, usage, permissions, aliases);
        this.plugin = plugin;
        this.lm = plugin.getLangManager();
    }

    /**
     * Checks if the specified location is inside of the world, in order to avoid claiming outside world.
     *
     * @param player the player to send the message
     * @return if the player's location is inside of the world
     */
    public boolean isInsideWorld(Player player) {
        return isInsideWorld(player, player.getLocation());
    }

    /**
     * Checks if the specified location is inside of the world, in order to avoid claiming outside world.
     *
     * @param player the player to send the message
     * @param chunk  the chunk's location to check if its inside of the world
     * @return if the location is inside of the world
     */
    public boolean isInsideWorld(Player player, Chunk chunk) {
        // + 8 allows to create a location at the "center" of the chunk. For y location, 100 is a random value.
        return isInsideWorld(player, new Location(chunk.getWorld(), (chunk.getX() << 4) + 8, 100, (chunk.getZ() << 4) + 8));
    }

    /**
     * Checks if the specified location is inside of the world, in order to avoid claiming outside world.
     *
     * @param player   the player to send the message
     * @param location the location to check if its inside of the world
     * @return if the location is inside of the world
     */
    public boolean isInsideWorld(Player player, Location location) {
        if (location.getWorld().getWorldBorder().isInside(location))
            return true;

        lm.sendMessage(player, lm.getString(player, "locOutsideWorld")
                .replace("%chunk%", plugin.getWGManager().getLandName(location))
                .replace("%world%", location.getWorld().getName()));
        return false;
    }

    /**
     * Checks if the player is in a disabled world and send him a message if he is.
     *
     * @return if the player is in a disabled world.
     */
    public boolean isDisabledWorld(Player player) {
        return JavaUtils.isDisabledWorld(lm, plugin, player, player.getWorld(), true);
    }

    /**
     * Checks if the world is disabled and sends a message to the player if the world is disabled.
     *
     * @param player the player to send the message
     * @param world  the world to check if its disabled
     * @return if the world is disabled
     */
    public boolean isDisabledWorld(Player player, World world) {
        return JavaUtils.isDisabledWorld(lm, plugin, player, world, true);
    }

    /**
     * Checks if the world is disabled and sends a message to the player if the world is disabled.
     *
     * @param world the world to check if its disabled
     * @return if the world is disabled
     */
    public boolean isDisabledWorld(World world) {
        return JavaUtils.isDisabledWorld(lm, plugin, null, world, false);
    }
}
