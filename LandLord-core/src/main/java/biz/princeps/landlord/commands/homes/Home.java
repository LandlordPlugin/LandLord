package biz.princeps.landlord.commands.homes;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import biz.princeps.lib.util.CommandDelayManager;
import com.google.common.collect.Sets;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 29/07/17
 */
public class Home extends LandlordCommand {

    public Home(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Home.name"),
                plugin.getConfig().getString("CommandSettings.Home.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Home.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Home.aliases")));

        CommandDelayManager delayManager = new CommandDelayManager(lm.getString("Commands.Home.dontMove"),
                lm.getString("Commands.Home.youMoved"),
                lm.getRawString("Commands.Home.countdown"),
                plugin.getConfig().getBoolean("Homes.spawnParticles"));

        int delay = plugin.getConfig().getInt("Homes.delay");
        for (String mainAlias : plugin.getConfig().getStringList("CommandSettings.Main.aliases")) {
            for (String homeAlias : plugin.getConfig().getStringList("CommandSettings.Home.aliases")) {
                delayManager.delayCommand("/" + mainAlias + " " + homeAlias, delay);
            }
            delayManager.delayCommand("/" + mainAlias + " " + plugin.getConfig().getString("CommandSettings.Home.name"), delay);
        }
    }


    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isPlayer()) {
            String target;
            try {
                target = arguments.get(0);
            } catch (ArgumentsOutOfBoundsException e) {
                target = "own";
            }
            onHome(properties, target);
        }
    }

    // requires permission landlord.player.home, if target equals own, else requires .homeother
    private void onHome(Properties props, String targetPlayer) {
        Player player = props.getPlayer();
        if (!Options.enabled_homes()) {
            lm.sendMessage(player, lm.getString(player, "Commands.SetHome.disabled"));
            return;
        }

        if (targetPlayer.equals("own")) {
            Location toGo = plugin.getPlayerManager().get(player.getUniqueId()).getHome();
            teleport(toGo, player, player.getName());
        } else {
            if (!player.hasPermission("landlord.player.homeother")) {
                lm.sendMessage(player, lm.getString(player, "noPermissions"));
                return;
            }

            plugin.getPlayerManager().getOffline(targetPlayer, (offline) -> {
                if (offline == null) {
                    lm.sendMessage(player, lm.getString(player, "Commands.Home.otherNoHome"));
                } else {
                    Location home = offline.getHome();
                    if (home == null) {
                        lm.sendMessage(player, lm.getString(player, "Commands.Home.otherNoHome"));
                        return;
                    }

                    // Do the actual teleport sync again.
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            teleport(home, player, targetPlayer);
                        }
                    }.runTask(plugin);
                }
            });
        }
    }


    private void teleport(Location toGo, Player player, String playerHome) {
        double cost = plugin.getConfig().getDouble("Homes.teleportCost");
        if (Options.isVaultEnabled()) {
            if (!plugin.getVaultManager().hasBalance(player, cost)) {
                lm.sendMessage(player, lm.getString(player, "Commands.Home.notEnoughMoney").replace("%cost%",
                        plugin.getVaultManager().format(cost)));
                return;
            }
        }

        if (toGo == null) {
            ComponentBuilder builder = new ComponentBuilder(lm.getString(player, "Commands.Home.noHome"));
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, PrincepsLib.getCommandManager()
                    .getCommand(Landlordbase.class).getCommandString(SetHome.class)));
            plugin.getUtilsManager().sendBasecomponent(player, builder.create());
            return;
        }

        if (cost > 0 && Options.isVaultEnabled()) {
            plugin.getVaultManager().take(player, cost);
            lm.sendMessage(player, lm.getString(player, "Commands.Home.costing")
                    .replace("%cost%", plugin.getVaultManager().format(cost)));
        }
        player.teleport(toGo);
        lm.sendMessage(player, lm.getString(player, "Commands.Home.welcomeHome").replace("%player%", playerHome));
    }
}
