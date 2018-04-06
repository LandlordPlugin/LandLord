package biz.princeps.landlord.commands.homes;

import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.util.CommandDelayManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 29/07/17
 */
public class Home extends LandlordCommand {

    public Home() {
        CommandDelayManager delayManager = new CommandDelayManager(lm.getString("Commands.Home.dontMove"),
                lm.getString("Commands.Home.youMoved"),
                lm.getRawString("Commands.Home.countdown"),
                plugin.getConfig().getBoolean("Homes.spawnParticles"));


        for (String mainAlias : plugin.getConfig().getStringList("CommandSettings.Main.aliases")) {
            for (String homeAlias : plugin.getConfig().getStringList("CommandSettings.Home.aliases")) {
                delayManager.delayCommand("/" + mainAlias + " " + homeAlias, plugin.getConfig().getInt("Homes.delay"));
            }
        }
    }

    // requires permission landlord.player.home, if target equals own, else requires .homeother
    public void onHome(Properties props, String targetPlayer) {
        Player player = props.getPlayer();
        if (!plugin.getConfig().getBoolean("Homes.enable")) {
            player.sendMessage(lm.getString("Commands.SetHome.disabled"));
            return;
        }

        if (targetPlayer.equals("own")) {
            Location toGo = plugin.getPlayerManager().get(player.getUniqueId()).getHome();
            teleport(toGo, player, player.getName());
        } else {
            if (!player.hasPermission("landlord.player.homeother")) {
                props.sendMessage(lm.getString("noPermissions"));
                return;
            }
            // TODO fix this ( which is again related to freaking cracked players, idc about this shit
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetPlayer);
            if(offlinePlayer == null){
                player.sendMessage(lm.getString("Commands.Home.otherNoHome"));
                return;
            }

            LPlayer offlineLPlayer = plugin.getPlayerManager().getOfflineLPlayer(offlinePlayer.getUniqueId());
            if(offlineLPlayer == null){
                player.sendMessage(lm.getString("Commands.Home.otherNoHome"));
                return;
            }

            Location home = offlineLPlayer.getHome();
            if(home == null){
                player.sendMessage(lm.getString("Commands.Home.otherNoHome"));
                return;
            }

            teleport(home, player, targetPlayer);
        }
    }


    private void teleport(Location toGo, Player player, String playerHome) {
        double cost = plugin.getConfig().getDouble("Homes.teleportCost");
        if (Options.isVaultEnabled()) {
            if (!plugin.getVaultHandler().hasBalance(player.getUniqueId(), cost)) {
                player.sendMessage(lm.getString("Commands.Home.notEnoughMoney").replace("%cost%", plugin.getVaultHandler().format(cost)));
                return;
            }
        }

        if (toGo == null) {
            ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Home.noHome"));
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll sethome"));
            player.spigot().sendMessage(builder.create());
            return;
        }

        if (cost > 0 && Options.isVaultEnabled()) {
            plugin.getVaultHandler().take(player.getUniqueId(), cost);
            player.sendMessage(lm.getString("Commands.Home.costing")
                    .replace("%cost%", plugin.getVaultHandler().format(cost)));
        }
        player.teleport(toGo);
        player.sendMessage(lm.getString("Commands.Home.welcomeHome").replace("%player%", playerHome));
    }
}
