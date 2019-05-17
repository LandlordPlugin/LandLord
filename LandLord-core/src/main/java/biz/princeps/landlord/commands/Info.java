package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.*;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

public class Info extends LandlordCommand {
    private IWorldGuardManager wg;

    public Info(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Info.name"),
                pl.getConfig().getString("CommandSettings.Info.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Info.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Info.aliases")));
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
            p.sendMessage("Free");
        } else if (land instanceof ISaleLandLand) {
            p.sendMessage("Sale");
        } else if (land instanceof IPossessedLand) {
            p.sendMessage("Possessed");
        } else {
            p.sendMessage("nothing");
        }

    }
}
