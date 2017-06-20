package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class ResetLand implements LandlordCommand {

    private Landlord plugin;

    public ResetLand(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Command for managing player land perms
     *
     * @param sender who executed the command
     * @param args   given with command
     * @return boolean
     */
    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String notOwner = messages.getString("info.warnings.notOwner");
        final String success = messages.getString("commands.reset.alerts.success");


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }
            Chunk currChunk = player.getLocation().getChunk();
            OwnedLand land = plugin.getLandManager().getApplicableLand(currChunk);
            if (land == null || (!land.getOwner().equals(player.getUniqueId()) && !player.hasPermission("landlord.admin.manage"))) {
                player.sendMessage(ChatColor.RED + notOwner);
                return true;
            }
            if (!land.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + notOwner);
                return true;
            }
            int price = plugin.getConfig().getInt("economy.resetPrice");
            if (args.length == 1) {
                String warning = plugin.getMessageConfig().getString("commands.reset.alerts.warning").replace("#{price}", String.valueOf(price));
                String clickToReset = plugin.getMessageConfig().getString("commands.reset.alerts.click");
                ComponentBuilder builder = new ComponentBuilder(warning)
                        .color(ChatColor.RED)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll reset confirm"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(clickToReset).color(ChatColor.RED).create()));
                sender.spigot().sendMessage(builder.create());
            }

            // /land reset confirm
            if (args.length == 2) {
                if (args[1].equals("confirm")) {
                    if (plugin.hasVault())
                        if (!plugin.getvHandler().chargeCash(player, price)) {
                            String missingMoney = plugin.getMessageConfig().getString("commands.reset.missingMoney");
                            player.sendMessage(ChatColor.RED + missingMoney.replace("#{price}", plugin.getvHandler().formatCash(price)).replace("#{money}", plugin.getvHandler().formatCash(plugin.getvHandler().getBalance(player.getUniqueId()))));
                            return true;
                        }
                    currChunk.getWorld().regenerateChunk(currChunk.getX(), currChunk.getZ());
                    player.sendMessage(ChatColor.GREEN + success);
                }
            }
        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.reset.usage");       // get the base usage string
        final String desc = messages.getString("commands.reset.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.reset.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
