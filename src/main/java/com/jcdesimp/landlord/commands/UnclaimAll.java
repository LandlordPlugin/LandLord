package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.List;

import static org.bukkit.Bukkit.getWorld;

/**
 * Created by jcdesimp on 2/17/15.
 * LandlordCommand object for players to unclaim land
 */
public class UnclaimAll implements LandlordCommand {

    private Landlord plugin;

    public UnclaimAll(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when landlord unclaim command is executed
     * This command must be run by a player
     *
     * @param sender who executed the command
     * @param args   given with command
     * @return boolean
     */
    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String noClaim = messages.getString("info.warnings.noClaim");
        final String noWorld = messages.getString("commands.unclaimall.alerts.noWorld");
        final String usage = messages.getString("commands.unclaimall.usage");
        final String notOwner = messages.getString("info.warnings.notOwner");
        final String landSold = messages.getString("commands.unclaimall.alerts.landSold");
        final String unclaimOther = messages.getString("commands.unclaimall.alerts.unclaimOther");
        final String unclaimed = messages.getString("commands.unclaimall.alerts.unclaimed");


        //is sender a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own") && !player.hasPermission("landlord.admin.unclaim")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }
            List<OwnedLand> landlist = plugin.getDatabase().getLands(player.getUniqueId());

            for (OwnedLand dbLand : landlist) {
                Chunk currChunk = dbLand.getChunk();

                if (plugin.hasVault()) {
                    if (plugin.getvHandler().hasEconomy()) {
                        Double amt = plugin.getConfig().getDouble("economy.sellPrice", 100.0);  //conf
                        if (amt > 0) {
                            int numFree = plugin.getConfig().getInt("economy.freeLand", 0);
                            if (numFree > 0 && plugin.getDatabase().getLands(player.getUniqueId()).size() <= numFree) {
                                //player.sendMessage(ChatColor.YELLOW+"You have been charged " + plugin.getvHandler().formatCash(amt) + " to purchase land.");
                            } else if (plugin.getvHandler().giveCash(player, amt)) {
                                player.sendMessage(ChatColor.GREEN + landSold.replace("#{amount}", plugin.getvHandler().formatCash(amt)));
                                //return true;
                            }
                        }

                    }
                }
                dbLand.delete();
                dbLand.highlightLand(player, Particle.SPELL_WITCH);

                sender.sendMessage(
                        ChatColor.YELLOW + unclaimed
                                .replace("#{chunkCoords}",
                                        "(" + currChunk.getX() + ", " + currChunk.getZ() + ")"
                                )
                                .replace("#{worldName}", currChunk.getWorld().getName())
                );

                //Regen land if enabled
                if (plugin.getConfig().getBoolean("options.regenOnUnclaim", false)) {
                    currChunk.getWorld().regenerateChunk(currChunk.getX(), currChunk.getZ());
                }

                if (plugin.getConfig().getBoolean("options.soundEffects", true)) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_HURT, 10, .5f);
                }
            }
            plugin.getMapManager().updateAll();

        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.unclaimall.usage");            // get the base usage string
        final String desc = messages.getString("commands.unclaimall.description");                        // get the description
        final String priceWarning = messages.getString("commands.unclaimall.alerts.priceWarning");       // get the price warning message
        final String regenWarning = messages.getString("commands.unclaimall.alerts.regenWarning");                 // get the chunk regen warning message

        String helpString = "";

        helpString += Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

        if (plugin.hasVault()) {
            if (plugin.getvHandler().hasEconomy() && plugin.getConfig().getDouble("economy.sellPrice", 50.0) > 0) { //conf
                helpString += ChatColor.YELLOW + " " + ChatColor.ITALIC + priceWarning
                        .replace("#{pricetag}",
                                plugin.getvHandler().formatCash(plugin.getConfig().getDouble("economy.sellPrice", 50.0))        //conf
                        );
            }
        }

        // add chunk regen warning if needed
        if (plugin.getConfig().getBoolean("options.regenOnUnclaim", false)) {
            helpString += ChatColor.RED + " " + ChatColor.ITALIC + regenWarning;
        }

        // return the constructed and colorized help string
        return helpString;
    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.unclaimall.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
