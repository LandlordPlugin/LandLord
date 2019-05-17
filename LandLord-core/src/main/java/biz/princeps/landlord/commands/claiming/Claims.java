package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
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


    public Claims(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Claims.name"),
                pl.getConfig().getString("CommandSettings.Claims.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Claims.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Claims.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {

        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();

        if (plugin.getConfig().getBoolean("Shop.enable")) {
            int claimcount = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();
            int regionCount = plugin.getWGManager().getRegionCount(player.getUniqueId());
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
                plugin.getUtilsManager().sendBasecomponent(player, text);
            }
        } else {
            lm.sendMessage(player, lm.getString("Commands.Claims.disabled"));
        }
    }
}
