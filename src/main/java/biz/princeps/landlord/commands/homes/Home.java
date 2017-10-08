package biz.princeps.landlord.commands.homes;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.util.CommandDelayManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 29.07.17.
 */
public class Home extends LandlordCommand {

    private CommandDelayManager delayManager;

    public Home() {
        this.delayManager = new CommandDelayManager(lm.getString("Commands.Home.dontMove"),
                lm.getString("Commands.Home.youMoved"),
                lm.getRawString("Commands.Home.countdown"),
                plugin.getConfig().getBoolean("Homes.spawnParticles"));
        this.delayManager.delayCommand("/ll home", plugin.getConfig().getInt("Homes.delay"));
    }

    // requires permission landlord.player.home, if target equals own, else requires .homeother
    public void onHome(Player player, String targetPlayer) {

        if (!plugin.getConfig().getBoolean("Homes.enable")) {
            player.sendMessage(lm.getString("Commands.SetHome.disabled"));
            return;
        }

        double cost = plugin.getConfig().getDouble("Homes.teleportCost");
        if (plugin.isVaultEnabled())
            if (!plugin.getVaultHandler().hasBalance(player.getUniqueId(), cost)) {
                player.sendMessage(lm.getString("Commands.Home.notEnoughMoney").replace("%cost%", plugin.getVaultHandler().format(cost)));
                return;
            }

        if (targetPlayer.equals("own")) {
            Location toGo = plugin.getPlayerManager().get(player.getUniqueId()).getHome();

            if (toGo == null) {
                ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Home.noHome"));
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll sethome"));
                player.spigot().sendMessage(builder.create());
                return;
            }

            if (cost > 0 && plugin.isVaultEnabled()) {
                plugin.getVaultHandler().take(player.getUniqueId(), cost);
                player.sendMessage(lm.getString("Commands.Home.costing")
                        .replace("%cost%", plugin.getVaultHandler().format(cost)));
            }
            player.teleport(toGo);
            player.sendMessage(lm.getString("Commands.Home.welcomeHome"));
        } else {


            // teleporting to other homes

        }

    }
}
