package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/27/20
 */
public class AdminClaim extends LandlordCommand {

    public AdminClaim(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.AdminClaim.name"),
                pl.getConfig().getString("CommandSettings.AdminClaim.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.AdminClaim.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.AdminClaim.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player sender = properties.getPlayer();
        String target;
        try {
            target = arguments.get(0);
        } catch (ArgumentsOutOfBoundsException e) {
            properties.sendUsage();
            return;
        }

        IOwnedLand land = plugin.getWGManager().getRegion(sender.getChunk());

        plugin.getPlayerManager().getOffline(target, (offline) -> {
            if (offline == null) {
                // Failure
                lm.sendMessage(sender, lm.getString("Commands.AdminClaim.noPlayer").replace("%player%", target));
            } else {
                // Success
                if (land != null) {
                    lm.sendMessage(sender, lm.getString("Commands.AdminClaim.alreadyOwned").replace("%land%", land.getName()));
                } else {
                    IOwnedLand land2 = plugin.getWGManager().claim(sender.getChunk(), offline.getUuid());
                    lm.sendMessage(sender, lm.getString("Commands.AdminClaim.success").replace("%land%", land2.getName()).replace("%name%", target));
                }
            }
        });
    }
}
