package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Update extends LandlordCommand {

    public Update(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Update.name"),
                plugin.getConfig().getString("CommandSettings.Update.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Update.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Update.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        try {
            if (arguments.get(0).equals("-r")) {
                onResetLands(properties.getCommandSender());
            }
        } catch (ArgumentsOutOfBoundsException e) {
            onUpdateLands(properties.getCommandSender());
        }
    }

    /**
     * Supposed to add missing flags to existing lands, remove non existing flags
     */
    private void onUpdateLands(CommandSender issuer) {
        //Don't make the server crash/lag with lots of regions
        new BukkitRunnable() {
            @Override
            public void run() {
                issuer.sendMessage("§8[§c§l!§8] §fStarting to update lands...");

                Collection<IOwnedLand> regions = new HashSet<>();
                for (World world : plugin.getPlugin().getServer().getWorlds()) {
                    regions.addAll(plugin.getWGManager().getRegions(world));
                }

                for (IOwnedLand region : regions) {
                    // update flags
                    region.updateFlags(region.getOwner());
                }
                issuer.sendMessage("§8[§c§l!§8] §fFinished updating lands!");
            }
        }.runTaskAsynchronously(plugin.getPlugin());
    }

    /**
     * Resets all lands to the default flag state
     */
    private void onResetLands(CommandSender sender) {
        //Don't make the server crash/lag with lots of regions
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage("§8[§c§l!§8] §fStarting to reset lands... Please wait :)");

                Collection<IOwnedLand> regions = new HashSet<>();
                for (World world : plugin.getPlugin().getServer().getWorlds()) {
                    if (isDisabledWorld(world))
                        continue;
                    regions.addAll(plugin.getWGManager().getRegions(world));
                }

                for (IOwnedLand region : regions) {
                    // reset flags
                    region.initFlags(region.getOwner());
                }

                sender.sendMessage("§8[§c§l!§8] §fFinished resetting lands!");
            }
        }.runTaskAsynchronously(plugin.getPlugin());
    }

}
