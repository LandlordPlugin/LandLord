package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.items.Maitem;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Author: Alex D. (SpatiumPrinceps)
 * <p>
 * Date: 11/7/17 10:45 AM
 */
public class LLItem extends LandlordCommand {

    public LLItem(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.MAItem.name"),
                plugin.getConfig().getString("CommandSettings.MAItem.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.MAItem.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.MAItem.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        String target = "self";

        if (arguments.size() > 0) {
            target = arguments.get(0);
        } else {
            if (properties.isPlayer()) {
                target = properties.getPlayer().getDisplayName();
            }
        }

        Player targetingPlayer = null;

        if (target == null) {
            if (properties.isPlayer()) {
                targetingPlayer = properties.getPlayer();
            }
        } else {
            targetingPlayer = plugin.getPlugin().getServer().getPlayer(target);
        }

        if (targetingPlayer == null) {
            if (properties.isPlayer()) {
                lm.sendMessage(properties.getPlayer(), lm.getString(properties.getPlayer(), "Commands.Item.noPlayer").replace("%player%", target));
            } else {
                properties.sendMessage(lm.getString(properties.getPlayer(), "Commands.Item.noPlayer").replace("%player%", target));
            }
            return;
        }

        // now we got our player for sure, lets give him the item
        Maitem item = new Maitem(plugin);
        item.give(targetingPlayer);
    }
}
