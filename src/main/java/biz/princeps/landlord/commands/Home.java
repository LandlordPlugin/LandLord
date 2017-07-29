package biz.princeps.landlord.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 29.07.17.
 */
public class Home extends LandlordCommand {

    // requires permission landlord.player.home, if target equals own, else requires .homeother
    public void onHome(Player player, String targetPlayer) {

        if (targetPlayer.equals("own")) {
            Location toGo = plugin.getPlayerManager().get(player.getUniqueId()).getHome();

            if (toGo == null) {
                ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Home.noHome"));
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll sethome"));
                player.spigot().sendMessage(builder.create());
                return;
            }
            // add slow teleport to lib with animation





        } else {


        }

    }
}
