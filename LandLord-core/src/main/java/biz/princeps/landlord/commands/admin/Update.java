package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Update extends LandlordCommand {

    private final ILandLord plugin;

    public Update(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Update.name"),
                plugin.getConfig().getString("CommandSettings.Update.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Update.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Update.aliases")));
        this.plugin = plugin;
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        try {
            String option = arguments.get(0);

            switch (option) {
                case "-u":
                    onUpdateLands(properties.getCommandSender());
                    break;
                case "-r":
                    onResetLands(properties.getCommandSender());
                    break;
                case "-c":
                    onReclaimLands(properties.getCommandSender());
                    break;
                default:
                    properties.sendUsage();
                    break;
            }
        } catch (ArgumentsOutOfBoundsException e) {
            properties.sendUsage();
        }
    }

    /**
     * Supposed to add missing flags to existing lands, remove non existing flags.
     */
    private void onUpdateLands(CommandSender issuer) {
        // Don't make the server crash/lag with lots of regions.
        new BukkitRunnable() {
            @Override
            public void run() {
                issuer.sendMessage("§8[§c§l!§8] §fStarting to update lands...");

                for (IOwnedLand ownedLand : getSusceptibleLands()) {
                    // Update flags.
                    ownedLand.updateFlags(ownedLand.getOwner());
                }

                issuer.sendMessage("§8[§c§l!§8] §fFinished updating lands!");
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Resets all lands to the default flag state.
     */
    private void onResetLands(CommandSender sender) {
        // Don't make the server crash/lag with lots of regions.
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage("§8[§c§l!§8] §fStarting to reset lands... Please wait :)");

                for (IOwnedLand ownedLand : getSusceptibleLands()) {
                    // Reset flags.
                    ownedLand.initFlags(ownedLand.getOwner());
                }

                sender.sendMessage("§8[§c§l!§8] §fFinished resetting lands!");
            }
        }.runTaskAsynchronously(plugin);
    }

    private void onReclaimLands(CommandSender sender) {
        // Don't make the server crash/lag with lots of regions.
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage("§8[§c§l!§8] §fStarting to reclaim lands... Please wait :)");

                for (IOwnedLand ownedLand : getSusceptibleLands()) {
                    // Reclaim the chunk.
                    ownedLand.reclaim();
                }

                sender.sendMessage("§8[§c§l!§8] §fFinished reclaiming lands!");
            }
        }.runTaskAsynchronously(plugin);
    }

    private Set<IOwnedLand> getSusceptibleLands() {
        return plugin.getServer().getWorlds().stream()
                .filter(world -> !isDisabledWorld(world))
                .map(world -> plugin.getWGManager().getRegions(world))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
