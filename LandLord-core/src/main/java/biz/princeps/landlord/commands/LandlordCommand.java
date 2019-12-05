package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.lib.command.SubCommand;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

    protected ILandLord plugin;
    protected ILangManager lm;

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
        return isInsideWorld(player, player.getChunk());
    }

    /**
     * Checks if the specified location is inside of the world, in order to avoid claiming outside world.
     *
     * @param player the player to send the message
     * @param chunk  the chunk's location to check if its inside of the world
     * @return if the location is inside of the world
     */
    public boolean isInsideWorld(Player player, Chunk chunk) {
        //+ 8 enables to create a location at the "center" of the chunk. y location : 100 is a random value
        if (chunk.getWorld().getWorldBorder().isInside(new Location(chunk.getWorld(), chunk.getX() * 16 + 8, 100, chunk.getZ() * 16 + 8)))
            return true;

        lm.sendMessage(player, lm.getString("locOutsideWorld")
                .replace("%chunk%", plugin.getWGManager().getLandName(chunk))
                .replace("%world%", chunk.getWorld().getName()));
        return false;
    }

    /**
     * Checks if the player is in a disabled world and send him a message if he is.
     *
     * @return if the player is in a disabled world.
     */
    public boolean isDisabledWorld(Player player) {
        return isDisabledWorld(player, player.getWorld());
    }

    /**
     * Checks if the world is disabled and sends a message to the player if the world is disabled.
     *
     * @param player the player to send the message
     * @param world  the world to check if its disabled
     * @return if the world is disabled
     */
    public boolean isDisabledWorld(Player player, World world) {
        List<String> stringList = plugin.getConfig().getStringList("disabled-worlds");

        for (String s : stringList) {
            if (Pattern.compile(s).matcher(world.getName()).matches()) {
                lm.sendMessage(player, lm.getString("Disabled-World"));
                return true;
            }
        }
        return false;
    }


}
