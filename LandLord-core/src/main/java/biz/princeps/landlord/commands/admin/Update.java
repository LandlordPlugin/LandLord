package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Update extends LandlordCommand {

    public Update(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Update.name"),
                pl.getConfig().getString("CommandSettings.Update.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Update.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Update.aliases")));
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
     * TODO implement this
     */
    private void onUpdateLands(CommandSender issuer) {

        issuer.sendMessage("Starting to update lands...");

        List<String> rawList = plugin.getConfig().getStringList("Flags");
        Set<String> to_set = new HashSet<>();
        for (String s : rawList) {
            try {
                to_set.add(s.split(" ")[0].toUpperCase());
            } catch (NumberFormatException ex) {
                Bukkit.getLogger().warning("ERROR: Your flag definition is invalid!");
            }
        }


        for (World world : Bukkit.getWorlds()) {
            Collection<IOwnedLand> regions = plugin.getWGProxy().getRegions(world);
            for (IOwnedLand region : regions) {
                // remove flags, that are no longer required
                for (ILLFlag iWrapperFlag : region.getFlags()) {
                    String flagname = iWrapperFlag.getName().toLowerCase();
                    if (!to_set.contains(flagname) &&
                            !flagname.equals("greeting") &&
                            !flagname.equals("farewell")) {

                        region.removeFlag(flagname);
                    }
                }

                // add missing flags
                for (String s : rawList) {
                    String[] s1 = s.split(":")[0].split(" ");
                    if (!region.containsFlag(s1[0].toLowerCase())) {
                        if (s1[2].equals("nonmembers")) {
                            //region.2(s1[0], s1[1]);
                        } else {
                            // region.addWGFlag(s1[0], s1[1]);
                        }
                    }
                }
                String name = Bukkit.getOfflinePlayer(region.getOwner()).getName();
                // add other flags
                if (!region.containsFlag("greeting")) {
                    region.setFlagValue("greeting",
                            lm.getRawString("Alerts.defaultGreeting").replace("%owner%", name));
                } else if (!region.containsFlag("farewell")) {
                    region.setFlagValue("farewell",
                            lm.getRawString("Alerts.defaultFarewell").replace("%owner%", name));
                }
            }
        }

        issuer.sendMessage("Finished updating lands!");
    }

    /**
     * Resets all lands to the default flag state
     */
    private void onResetLands(CommandSender sender) {
        /*
        sender.sendMessage("Starting to reset lands...");
        List<String> rawList = Landlord.getInstance().getConfig().getStringList("Flags");

        for (World w : Bukkit.getWorlds()) {
            for (World world : Bukkit.getWorlds()) {
                Collection<IOwnedLand> regions = plugin.getWgproxy().getRegions(world);
                for (IOwnedLand region : regions) {
                    // add missing flags
                    for (String s : rawList) {
                        String[] s1 = s.split(":")[0].split(" ");
                        if (!region.containsFlag(s1[0])) {
                            if (s1[2].equals("nonmembers")) {
                                region.addRegionGroupFlag(s1[0], s1[1]);
                            } else {
                                region.addWGFlag(s1[0], s1[1]);
                            }
                        }
                    }
                }
            }
        }
        sender.sendMessage("Finished resetting lands!");
        */
    }

}
