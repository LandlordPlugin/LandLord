package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.command.CommandSender;

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
     */
    private void onUpdateLands(CommandSender issuer) {
        /*
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

        Collection<IOwnedLand> regions = new HashSet<>();
        Bukkit.getWorlds().forEach(w -> regions.addAll(plugin.getWGProxy().getRegions(w)));

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
                    region.setFlagValue(s1[0].toLowerCase(), s1[2], s1[1]);
                }
            }
            String name = Bukkit.getOfflinePlayer(region.getOwner()).getName();
            // add other flags
            if (!region.containsFlag("greeting")) {
                region.setFlagValue("greeting", null,
                        lm.getRawString("Alerts.defaultGreeting").replace("%owner%", name));
            } else if (!region.containsFlag("farewell")) {
                region.setFlagValue("farewell", null,
                        lm.getRawString("Alerts.defaultFarewell").replace("%owner%", name));
            }
        }
*/
        issuer.sendMessage("Finished updating lands!");
    }

    /**
     * Resets all lands to the default flag state
     */
    private void onResetLands(CommandSender sender) {
/*
        sender.sendMessage("Starting to reset lands...");
        List<String> rawList = plugin.getConfig().getStringList("Flags");

        Collection<IOwnedLand> regions = new HashSet<>();
        Bukkit.getWorlds().forEach(w -> regions.addAll(plugin.getWGProxy().getRegions(w)));

        for (IOwnedLand region : regions) {
            // add missing flags
            for (String s : rawList) {
                String[] toSet = s.split(":")[0].split(" ");
                region.setFlagValue(toSet[0], toSet[2], toSet[1]);
            }
        }

        sender.sendMessage("Finished resetting lands!");
*/
    }

}
