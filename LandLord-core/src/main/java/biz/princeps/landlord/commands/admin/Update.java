package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.commands.LandlordCommand;
import org.bukkit.command.CommandSender;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Update extends LandlordCommand {

    /**
     * Supposed to add missing flags to existing lands, remove non existing flags
     * TODO implement this
     */
    public void onUpdateLands(CommandSender issuer) {
        /*
        issuer.sendMessage("Starting to update lands...");

        List<String> rawList = Landlord.getInstance().getConfig().getStringList("Flags");
        Set<String> to_set = new HashSet<>();
        for (String s : rawList) {
            try {
                to_set.add(s.split(" ")[0].toUpperCase());
            } catch (NumberFormatException ex) {
                Bukkit.getLogger().warning("ERROR: Your flag definition is invalid!");
            }
        }


        for (World world : Bukkit.getWorlds()) {
            Collection<IOwnedLand> regions = plugin.getWgproxy().getRegions(world);
            for (IOwnedLand region : regions) {
                // remove flags, that are no longer required
                for (IWrapperFlag iWrapperFlag : new HashSet<>(region.getFlags())) {
                    if (!to_set.contains(iWrapperFlag.getName()) &&
                            !iWrapperFlag.getName().equals("GREET_MESSAGE") &&
                            !iWrapperFlag.getName().equals("FAREWELL_MESSAGE")) {
                        region.removeFlag(iWrapperFlag);
                    }
                }

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
                String name = Bukkit.getOfflinePlayer(region.getOwner()).getName();
                // add other flags
                if (!region.containsFlag("GREET_MESSAGE")) {
                    region.addWGFlag("GREET_MESSAGE",
                            lm.getRawString("Alerts.defaultGreeting").replace("%owner%", name));
                } else if (!region.containsFlag("FAREWELL_MESSAGE")) {
                    region.addWGFlag("FAREWELL_MESSAGE",
                            lm.getRawString("Alerts.defaultFarewell").replace("%owner%", name));
                }
            }
        }

        issuer.sendMessage("Finished updating lands!");
        */
    }

    /**
     * Resets all lands to the default flag state
     */
    public void onResetLands(CommandSender sender) {
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
