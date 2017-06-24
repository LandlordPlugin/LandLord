package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by jcdesimp on 2/18/15.
 * Command for a user to remove a friend from all land at once.
 */
public class UnfriendAll implements LandlordCommand {

    private Landlord plugin;

    public UnfriendAll(Landlord plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String usage = messages.getString("commands.unfriendAll.usage");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String unknownPlayer = messages.getString("info.warnings.unknownPlayer");
        final String playerRemoved = messages.getString("commands.unfriendAll.alerts.playerRemoved");
        final String noLand = messages.getString("commands.unfriendAll.alerts.noLand");

        //is sender a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + usage.replace("#label}", label).replace("#{command}", args[0]));
            }
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }


            OfflinePlayer possible = Bukkit.getOfflinePlayer(args[1]);
            if (!possible.hasPlayedBefore() && !possible.isOnline()) {
                player.sendMessage(ChatColor.RED + unknownPlayer);
                return true;
            }
            List<OwnedLand> pLand = plugin.getDatabase().getLands(player.getUniqueId());

            if (pLand.size() > 0) {
                for (OwnedLand l : pLand) {
                    System.out.println(l.getLandId());
                    l.removeFriend(possible.getUniqueId());
                    Landlord.getInstance().getLandManager().refresh(l.getData());
                    //plugin.getLandManager().updateFriends(l.getData(), l.getFriends());
                }

                player.sendMessage(ChatColor.GREEN + playerRemoved.replace("#{playername}", args[1]));
                return true;
            } else {
                player.sendMessage(ChatColor.YELLOW + noLand);
            }

        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.unfriendAll.usage"); // get the base usage string
        final String desc = messages.getString("commands.unfriendAll.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.unfriendAll.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
