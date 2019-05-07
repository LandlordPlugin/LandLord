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
            int regionCount = plugin.getWgproxy().getRegionCount(player.getUniqueId());
            String message = lm.getString("Commands.Claims.message");
            String noClaims = lm.getString("Commands.Claims.noClaims");


            if (claimcount > 0) {
                lm.sendMessage(player, message.replace("%regions%", regionCount + "")
                        .replace("%claims%", claimcount + ""));
            } else {
                BaseComponent[] text = TextComponent.fromLegacyText(noClaims);
                for (BaseComponent baseComponent : text) {
                    baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll shop"));
                }
                plugin.getUtilsProxy().send_basecomponent(player , text);
            }
        } else {
            lm.sendMessage(player, lm.getString("Commands.Claims.disabled"));
        }
    }


}
