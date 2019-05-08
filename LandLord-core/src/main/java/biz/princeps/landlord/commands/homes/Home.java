package biz.princeps.landlord.commands.homes;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.util.CommandDelayManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 29/07/17
 */
public class Home extends LandlordCommand {

    public Home(ILandLord pl) {
        super(pl);
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
        if (!Options.enabled_homes()) {
            lm.sendMessage(player, lm.getString("Commands.SetHome.disabled"));
            return;
        }

        if (targetPlayer.equals("own")) {
            Location toGo = plugin.getPlayerManager().get(player.getUniqueId()).getHome();
            teleport(toGo, player, player.getName());
        } else {
            if (!player.hasPermission("landlord.player.homeother")) {
                lm.sendMessage(player, lm.getString("noPermissions"));
                return;
            }

            plugin.getPlayerManager().getOfflinePlayerAsync(targetPlayer, lPlayer -> {
                if (lPlayer == null) {
                    lm.sendMessage(player, lm.getString("Commands.Home.otherNoHome"));
                } else {
                    Location home = lPlayer.getHome();
                    if (home == null) {
                        lm.sendMessage(player, lm.getString("Commands.Home.otherNoHome"));
                        return;
                    }

                    // do the actual teleport sync again
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getPlugin(), () -> teleport(home, player, targetPlayer));
                }
            });
        }
    }


    private void teleport(Location toGo, Player player, String playerHome) {
        double cost = plugin.getConfig().getDouble("Homes.teleportCost");
        if (Options.isVaultEnabled()) {
            if (!plugin.getVaultManager().hasBalance(player.getUniqueId(), cost)) {
                lm.sendMessage(player, lm.getString("Commands.Home.notEnoughMoney").replace("%cost%", plugin.getVaultManager().format(cost)));
                return;
            }
        }

        if (toGo == null) {
            ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Home.noHome"));
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll sethome"));
            plugin.getUtilsProxy().send_basecomponent(player,builder.create());
            return;
        }

        if (cost > 0 && Options.isVaultEnabled()) {
            plugin.getVaultManager().take(player.getUniqueId(), cost);
            lm.sendMessage(player, lm.getString("Commands.Home.costing")
                    .replace("%cost%", plugin.getVaultManager().format(cost)));
        }
        player.teleport(toGo);
        lm.sendMessage(player, lm.getString("Commands.Home.welcomeHome").replace("%player%", playerHome));
    }
}
