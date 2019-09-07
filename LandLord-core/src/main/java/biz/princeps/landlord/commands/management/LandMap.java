package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/7/17
 */
public class LandMap extends LandlordCommand {

    public LandMap(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Map.name"),
                pl.getConfig().getString("CommandSettings.Map.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Map.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Map.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {

        if (arguments.size() == 0) {
            // toggle
            if (properties.isPlayer()) {
                onToggleLandMap(properties.getPlayer());
            }
        } else if (arguments.size() == 1) {
            // on/off
            String arg = arguments.get()[0];
            if (arg.toLowerCase().equals("on") || arg.toLowerCase().equals("off")) {
                onToggleLandMap(properties.getPlayer(), arg.toLowerCase());
            }
        }
    }

    private void onToggleLandMap(Player player) {

        if (isDisabledWorld(player)) return;

        if (Options.enabled_map())
            plugin.getMapManager().toggleMap(player);
        else {
            lm.sendMessage(player, lm.getString("Commands.LandMap.disabled"));
        }

    }

    private void onToggleLandMap(Player player, String state) {
        if (isDisabledWorld(player)) return;

        if (!Options.enabled_map()) {
            lm.sendMessage(player, lm.getString("Commands.LandMap.disabled"));
            return;
        }

        if (state.equals("on")) {
            plugin.getMapManager().addMap(player);
        } else {
            plugin.getMapManager().removeMap(player);
        }
    }
}
