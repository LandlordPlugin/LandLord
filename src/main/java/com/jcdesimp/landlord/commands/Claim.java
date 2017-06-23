package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by jcdesimp on 2/17/15.
 * LandlordCommand object that lets a user Claim land
 */
public class Claim implements LandlordCommand {


    private Landlord plugin;


    /**
     * Constructor for Claim command
     *
     * @param plugin the main Landlord plugin
     */
    public Claim(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when landlord claim command is executed
     * This command must be run by a player
     *
     * @param sender who executed the command
     * @param args   given with command
     * @return boolean
     */
    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");     // When run by non-player
        final String noPerms = messages.getString("info.warnings.noPerms");     // No permissions

        final String cannotClaim = messages.getString("info.warnings.noClaim");     // Claiming disabled in this world
        final String alreadyOwn = messages.getString("commands.claim.alerts.alreadyOwn");       // When you already own this land
        final String otherOwn = messages.getString("commands.claim.alerts.otherOwn");       // Someone else owns this land
        final String noClaimZone = messages.getString("commands.claim.alerts.noClaimZone");     // You can't claim here! (Worldguard)
        final String ownLimit = messages.getString("commands.claim.alerts.ownLimit");       // Chunk limit hit
        final String claimPrice = messages.getString("commands.claim.alerts.claimPrice");       // Not enough funds
        final String charged = messages.getString("commands.claim.alerts.charged");     // Charged for claim
        final String success = messages.getString("commands.claim.alerts.success");     // Chunk claim successful

        //is sender a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }


            //sender.sendMessage(ChatColor.GOLD + "Current Location: " + player.getLocation().toString());
            Chunk currChunk = player.getLocation().getChunk();

            List<String> disabledWorlds = plugin.getConfig().getStringList("disabled-worlds");
            for (String s : disabledWorlds) {
                if (s.equalsIgnoreCase(currChunk.getWorld().getName())) {
                    player.sendMessage(ChatColor.RED + cannotClaim);
                    return true;
                }
            }

            // Check if worldguard is installed
            if (plugin.hasWorldGuard()) {
                // if it is make sure that the attempted land claim isn't with a protected worldguard region.
                if (!plugin.getWgHandler().canClaim(player, currChunk)) {
                    player.sendMessage(ChatColor.RED + noClaimZone);
                    return true;
                }
            }


            OwnedLand land = plugin.getLandManager().getApplicableLand(player.getLocation());


            if (land != null) {
                //Check if they already own this land
                if (land.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + alreadyOwn);
                    return true;
                }
                player.sendMessage(ChatColor.YELLOW + otherOwn);
                return true;
            }
            int orLimit = plugin.getConfig().getInt("limits.landLimit", 10);
            int limit = plugin.getConfig().getInt("limits.landLimit", 10);

            if (player.hasPermission("landlord.limit.extra5")) {
                limit = orLimit + plugin.getConfig().getInt("limits.extra5", 0);
            } else if (player.hasPermission("landlord.limit.extra4")) {
                limit = orLimit + plugin.getConfig().getInt("limits.extra4", 0);
            } else if (player.hasPermission("landlord.limit.extra3")) {
                limit = orLimit + plugin.getConfig().getInt("limits.extra3", 0);
            } else if (player.hasPermission("landlord.limit.extra2")) {
                limit = orLimit + plugin.getConfig().getInt("limits.extra2", 0);
            } else if (player.hasPermission("landlord.limit.extra")) {
                limit = orLimit + plugin.getConfig().getInt("limits.extra", 0);
            }

            List<OwnedLand> ownedLands = plugin.getDatabase().getLands(player.getUniqueId());

            if (limit >= 0 && !player.hasPermission("landlord.limit.override")) {
                if (ownedLands.size() >= limit) {
                    player.sendMessage(ChatColor.RED + ownLimit.replace("#{limit}", "" + limit));
                    return true;
                }
            }

            //Money Handling
            if (plugin.hasVault()) {
                if (plugin.getvHandler().hasEconomy()) {
                    Double amt = plugin.getConfig().getDouble("economy.buyPrice", 100.0);
                    if (amt > 0) {
                        int numFree = plugin.getConfig().getInt("economy.freeLand", 0);
                        if (numFree > 0 && ownedLands.size() < numFree) {
                            //player.sendMessage(ChatColor.YELLOW+"You have been charged " + plugin.getvHandler().formatCash(amt) + " to purchase land.");
                        } else if (!plugin.getvHandler().chargeCash(player, amt)) {
                            player.sendMessage(ChatColor.RED + claimPrice.replace("#{cost}", plugin.getvHandler().formatCash(amt)));
                            return true;
                        } else {
                            player.sendMessage(ChatColor.YELLOW + charged.replace("#{cost}", plugin.getvHandler().formatCash(amt)));
                        }
                    }

                }
            }

            land = plugin.getLandManager().createNewLand(player.getUniqueId(), currChunk);
            // player.sendMessage(land.getOwnerUsername() + land.getChunk().getWorld() + land.getChunk().getZ() + ":" + land.getChunk().getX());
            land.save();
            OwnedLand.highlightLand(player, Particle.VILLAGER_HAPPY);
            sender.sendMessage(
                    ChatColor.GREEN + success
                            .replace("#{chunkCoords}", "(" + currChunk.getX() + ", " + currChunk.getZ() + ")")
                            .replace("#{worldName}", currChunk.getWorld().getName()));

            if (plugin.getConfig().getBoolean("options.soundEffects", true)) {
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, 10, 10);
            }


            plugin.getMapManager().updateAll();
            //sender.sendMessage(ChatColor.DARK_GREEN + "Land claim command executed!");
        }
        return true;
    }

    public String getHelpText(CommandSender sender) {
        FileConfiguration messages = plugin.getMessageConfig();

        String usage = messages.getString("commands.claim.usage");                      // get the base usage string
        String desc = messages.getString("commands.claim.description");                      // get the description
        String priceWarning = messages.getString("commands.claim.alerts.cost");    // get the price warning message


        String helpString = ""; // start building the help string

        helpString += Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

        if (plugin.hasVault()) {
            if (plugin.getvHandler().hasEconomy() && plugin.getConfig().getDouble("economy.buyPrice", 100.0) > 0) {     //conf
                helpString += ChatColor.YELLOW + " " + ChatColor.ITALIC + priceWarning
                        .replace(
                                "#{pricetag}",                  // insert the formatted price string
                                plugin.getvHandler().formatCash(plugin.getConfig().getDouble("economy.buyPrice", 100.0))        //conf
                        );
            }
        }


        // return the constructed and colorized help string
        return helpString;

    }

    public String[] getTriggers() {
        List<String> triggers = plugin.getMessageConfig().getStringList("commands.claim.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
