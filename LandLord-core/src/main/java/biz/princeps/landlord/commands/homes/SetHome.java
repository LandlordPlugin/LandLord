package biz.princeps.landlord.commands.homes;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 29/07/17
 */
public class SetHome extends LandlordCommand {

    public SetHome(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Sethome.name"),
                plugin.getConfig().getString("CommandSettings.Sethome.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Sethome.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Sethome.aliases")));
    }

    // requires permission landlord.player.home
    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();

        //Don't sethome outside of the world
        if (!isInsideWorld(player)) {
            return;
        }

        if (!Options.enabled_homes()) {
            lm.sendMessage(player, lm.getString(player, "Commands.SetHome.disabled"));
            return;
        }

        if (isDisabledWorld(player))
            return;

        IOwnedLand land = plugin.getWGManager().getRegion(player.getLocation());

        if (land == null) {
            lm.sendMessage(player, lm.getString(player, "Commands.SetHome.nullLand"));
            return;
        }

        if (!land.isOwner(player.getUniqueId())) {
            lm.sendMessage(player, lm.getString(player, "Commands.SetHome.notOwn")
                    .replace("%owner%", land.getOwnersString()));
            return;
        }

        plugin.getPlayerManager().get(player.getUniqueId()).setHome(player.getLocation());
        lm.sendMessage(player, lm.getString(player, "Commands.SetHome.success"));
    }
}
