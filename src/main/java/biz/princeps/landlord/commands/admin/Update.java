package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.commands.LandlordCommand;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Update extends LandlordCommand {

    /**
     * Supposed to add missing flags to existing lands, remove non existing flags
     */
    public void onUpdateLands(CommandSender issuer) {
        issuer.sendMessage("Starting to update lands...");

        for (World w : Bukkit.getWorlds()) {
            for (ProtectedRegion pr : plugin.getWgHandler().getRegionManager(w).getRegions().values()) {

                if (plugin.isLLRegion(pr.getId())) {
                    List<String> flaggy = Landlord.getInstance().getConfig().getStringList("Flags");
                    Set<String> flags = new HashSet<>();

                    flaggy.forEach(s -> flags.add(s.split(" ")[0]));

                    //Iterate over all existing flags
                    for (Flag<?> flag : plugin.getWgHandler().getFlags()) {
                        if (flag instanceof StateFlag) {
                            boolean failed = false;
                            if (flags.contains(flag.getName())) {
                                // Filters the config list for the right line and split that line in the mid at :
                                String[] rules = flaggy.stream().filter(s -> s.startsWith(flag.getName())).findFirst().get().split(":");
                                if (rules.length == 2) {

                                    if (!pr.getFlags().containsKey(flag)) {
                                        String[] defSplit = rules[0].split(" ");
                                        if (defSplit.length == 3) {
                                            StateFlag.State state = StateFlag.State.valueOf(defSplit[1].toUpperCase());
                                            if (defSplit[2].equals("nonmembers"))
                                                pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);

                                            pr.setFlag((StateFlag) flag, state);
                                        } else {
                                            failed = true;
                                        }
                                    }
                                } else {
                                    failed = true;
                                }
                            } else {
                                pr.getFlags().remove(flag);
                            }

                            if (failed) {
                                Bukkit.getLogger().warning("ERROR: Your flag definition is invalid!");
                                break;
                            }
                        }
                    }


                    String name = Bukkit.getOfflinePlayer(pr.getOwners().getUniqueIds().iterator().next()).getName();

                    String greeting = lm.getRawString("Alerts.defaultGreeting").replace("%owner%", name);
                    String farewell = lm.getRawString("Alerts.defaultFarewell").replace("%owner%", name);

                    if (!pr.getFlags().containsKey(DefaultFlag.GREET_MESSAGE)) {
                        pr.setFlag(DefaultFlag.GREET_MESSAGE, greeting);
                    }
                    if (!pr.getFlags().containsKey(DefaultFlag.FAREWELL_MESSAGE)) {
                        pr.setFlag(DefaultFlag.FAREWELL_MESSAGE, farewell);
                    }
                }

            }
        }
        issuer.sendMessage("Finished updating lands!");
    }

    /**
     * Resets all lands to the default flag state
     */

    public void onResetLands(CommandSender sender) {
        sender.sendMessage("Starting to reset lands...");

        for (World w : Bukkit.getWorlds()) {
            for (ProtectedRegion pr : plugin.getWgHandler().getRegionManager(w).getRegions().values()) {

                if (plugin.isLLRegion(pr.getId())) {

                    List<String> flaggy = Landlord.getInstance().getConfig().getStringList("Flags");
                    Set<String> flags = new HashSet<>();

                    flaggy.forEach(s -> flags.add(s.split(" ")[0]));

                    //Iterate over all existing flags
                    for (Flag<?> flag : plugin.getWgHandler().getFlags()) {
                        if (flag instanceof StateFlag) {
                            boolean failed = false;
                            if (flags.contains(flag.getName())) {
                                // Filters the config list for the right line and split that line in the mid at :
                                String[] rules = flaggy.stream().filter(s -> s.startsWith(flag.getName())).findFirst().get().split(":");
                                if (rules.length == 2) {

                                    String[] defSplit = rules[0].split(" ");
                                    if (defSplit.length == 3) {
                                        StateFlag.State state = StateFlag.State.valueOf(defSplit[1].toUpperCase());
                                        if (defSplit[2].equals("nonmembers"))
                                            pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);

                                        pr.setFlag((StateFlag) flag, state);
                                    } else {
                                        failed = true;
                                    }

                                } else {
                                    failed = true;
                                }
                            } else {
                                pr.getFlags().remove(flag);
                            }

                            if (failed) {
                                Bukkit.getLogger().warning("ERROR: Your flag definition is invalid!");
                                break;
                            }
                        }
                    }


                    String name = Bukkit.getOfflinePlayer(pr.getOwners().getUniqueIds().iterator().next()).getName();

                    String greeting = lm.getRawString("Alerts.defaultGreeting").replace("%owner%", name);
                    String farewell = lm.getRawString("Alerts.defaultFarewell").replace("%owner%", name);

                    String actualGreeting = pr.getFlag(DefaultFlag.GREET_MESSAGE);
                    String actualFarewell = pr.getFlag(DefaultFlag.FAREWELL_MESSAGE);

                    if (!greeting.equals(actualGreeting)) {
                        pr.setFlag(DefaultFlag.GREET_MESSAGE, greeting);
                    }
                    if (!farewell.equals(actualFarewell)) {
                        pr.setFlag(DefaultFlag.FAREWELL_MESSAGE, farewell);
                    }
                }

            }
        }
        sender.sendMessage("Finished resetting lands!");
    }
}