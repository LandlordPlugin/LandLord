package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.lib.PrincepsLib;
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


    public Claims(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Claims.name"),
                plugin.getConfig().getString("CommandSettings.Claims.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Claims.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Claims.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {

        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();

        if (plugin.getConfig().getBoolean("Claims.enable")) {
            int claimcount = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();
            int regionCount = plugin.getWGManager().getRegionCount(player.getUniqueId());

            if (claimcount > 0) {
                lm.sendMessage(player, lm.getString(player, "Commands.Claims.message").replace("%regions%", regionCount + "")
                        .replace("%claims%", claimcount + ""));
            } else {
                BaseComponent[] text = TextComponent.fromLegacyText(lm.getString(player, "Commands.Claims.noClaims"));
                for (BaseComponent baseComponent : text) {
                    baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            PrincepsLib.getCommandManager().getCommand(Landlordbase.class).getCommandString(Shop.class)));
                }
                plugin.getUtilsManager().sendBasecomponent(player, text);
            }
        } else {
            int highestAllowedLandCount = plugin.getPlayerManager().getMaxClaimPermission(player);
            int regionCount = plugin.getWGManager().getRegionCount(player.getUniqueId());
            lm.sendMessage(player, lm.getString(player, "Commands.Claims.message").replace("%regions%", regionCount + "")
                    .replace("%claims%", (highestAllowedLandCount == Integer.MAX_VALUE ? "∞" : highestAllowedLandCount <= 0 ? "∅" : highestAllowedLandCount + "")));
        }
    }
}
