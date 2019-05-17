package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.*;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import java.util.Set;

public class Claim extends LandlordCommand {

    private IWorldGuardManager wg;

    public Claim(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Claim.name"),
                pl.getConfig().getString("CommandSettings.Claim.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Claim.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Claim.aliases")));
        this.wg = pl.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {

        if (properties.isConsole()) {
            return;
        }

        Player p = properties.getPlayer();
        if (isDisabledWorld(p)) {
            return;
        }

        ILand land = wg.getRegion(p.getLocation());

        if (land instanceof IFreeLand) {
            ((IFreeLand) land).claim(p.getUniqueId());

        } else if (land instanceof ISaleLandLand) {
            p.sendMessage("Sale");


        } else if (land instanceof IPossessedLand) {
            IPossessedLand ol = ((IPossessedLand) land);

            lm.sendMessage(p, lm.getString("Commands.Claim.alreadyClaimed")
                    .replace("%owner%", ol.getOwnerName()));
            return;
        }


    }
}
