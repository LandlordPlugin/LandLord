package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.commands.LandlordCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 28/07/2017
 */
public class Claims extends LandlordCommand {


    public void onClaims(Player player) {

        if (plugin.getConfig().getBoolean("Shop.enable")) {
            int claimcount = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();
            int regionCount = plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(plugin.getWgHandler().getWG().wrapPlayer(player));

            String message = lm.getString("Commands.Claims.message");
            String noClaims = lm.getString("Commands.Claims.noClaims");


            if (claimcount > 0) {
                player.sendMessage(message.replace("%regions%", regionCount + "")
                        .replace("%claims%", claimcount + ""));
            } else {
                BaseComponent text = new TextComponent(noClaims);
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll shop"));
                player.spigot().sendMessage(text);
            }
        } else {
            player.sendMessage(lm.getString("Commands.Claims.disabled"));
        }
    }


}
