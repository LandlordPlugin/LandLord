package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.Bukkit.getWorld;

/**
 * Created by jcdesimp on 2/17/15.
 * LandlordCommand object for players to unclaim land
 */
public class Unclaim implements LandlordCommand {

    private Landlord plugin;

    public Unclaim(Landlord plugin) {
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
        final String noWorld = messages.getString("commands.unclaim.alerts.noWorld");
        final String usage = messages.getString("commands.unclaim.usage");
        final String notOwner = messages.getString("info.warnings.notOwner");
        final String landSold = messages.getString("commands.unclaim.alerts.landSold");
        final String unclaimOther = messages.getString("commands.unclaim.alerts.unclaimOther");
        final String unclaimed = messages.getString("commands.unclaim.alerts.unclaimed");


        //is sender a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own") && !player.hasPermission("landlord.admin.unclaim")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }
            //sender.sendMessage(ChatColor.GOLD + "Current Location: " + player.getLocation().toString());
            Chunk currChunk = player.getLocation().getChunk();

            int x = currChunk.getX();
            int z = currChunk.getZ();
            String worldname = currChunk.getWorld().getName();

            List<String> disabledWorlds = plugin.getConfig().getStringList("disabled-worlds");  //conf
            for (String s : disabledWorlds) {
                if (s.equalsIgnoreCase(currChunk.getWorld().getName())) {
                    player.sendMessage(ChatColor.RED + noClaim);
                    return true;
                }
            }


            if (args.length > 1) {
                try {
                    String[] coords = args[1].split(",");
                    //System.out.println("COORDS: "+coords);
                    x = Integer.parseInt(coords[0]);
                    z = Integer.parseInt(coords[1]);
                    currChunk = currChunk.getWorld().getChunkAt(x, z);
                    if (args.length > 2) {

                        if (plugin.getServer().getWorld(worldname) == null) {
                            player.sendMessage(ChatColor.RED + noWorld.replace("#{worldName}", "'" + worldname + "'"));
                            return true;
                        }
                        currChunk = getWorld(worldname).getChunkAt(x, z);

                    }
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    player.sendMessage(ChatColor.RED + usage
                            .replace("#{label}", label)
                            .replace("#{command}", args[0])
                    );
                    return true;

                } catch (ArrayIndexOutOfBoundsException e) {
                    player.sendMessage(ChatColor.RED + usage
                            .replace("#{label}", label)
                            .replace("#{command}", args[0])
                    );
                    return true;
                }
            }
            OwnedLand dbLand = plugin.getLandManager().getLandFromCache(worldname, x, z);


            if (dbLand == null || (!dbLand.getOwner().equals(player.getUniqueId()) && !player.hasPermission("landlord.admin.unclaim"))) {
                player.sendMessage(ChatColor.RED + notOwner);
                return true;
            }
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
            if (!player.getUniqueId().equals(dbLand.getOwner())) {
                player.sendMessage(ChatColor.YELLOW + unclaimOther.replace("#{player}", Bukkit.getOfflinePlayer(dbLand.getOwner()).getName()));
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
            plugin.getMapManager().updateAll();
        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.unclaim.usage");            // get the base usage string
        final String desc = messages.getString("commands.unclaim.description");                        // get the description
        final String priceWarning = messages.getString("commands.unclaim.alerts.priceWarning");       // get the price warning message
        final String regenWarning = messages.getString("commands.unclaim.alerts.regenWarning");                 // get the chunk regen warning message

        String helpString = "";

        helpString += Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

        if (plugin.hasVault()) {
            if (plugin.getvHandler().hasEconomy() && plugin.getConfig().getDouble("economy.sellPrice", 50.0) > 0) { //conf
                helpString += ChatColor.YELLOW + " " + ChatColor.ITALIC + priceWarning
                        .replace(
                                "#{pricetag}",                  // insert the formatted price string
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
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.unclaim.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
