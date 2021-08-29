package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Advertise extends LandlordCommand {

    private final IWorldGuardManager wg;

    public Advertise(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Advertise.name"),
                pl.getConfig().getString("CommandSettings.Advertise.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Advertise.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Advertise.aliases")));
        wg = pl.getWGManager();
    }


    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        if (!Options.isVaultEnabled()) {
            return;
        }

        try {
            String landname = "this";
            double price;
            if (arguments.size() > 1) {
                landname = arguments.get(0);
                price = arguments.getDouble(1);
            } else {
                price = arguments.getDouble(0);
            }

            onAdvertise(properties.getPlayer(), landname, price);
        } catch (ArgumentsOutOfBoundsException e) {
            properties.sendUsage();
        }
    }


    private void onAdvertise(Player player, String landname, double price) {
        IOwnedLand iOwnedLand;
        if (landname.equals("this")) {
            iOwnedLand = wg.getRegion(player.getLocation());
        } else {
            iOwnedLand = wg.getRegion(landname);
        }

        if (iOwnedLand == null) {
            lm.sendMessage(player, lm.getString(player, "Commands.Advertise.notOwnFreeLand"));
            return;
        }

        if (isDisabledWorld(player, iOwnedLand.getWorld())) {
            return;
        }

        if (!iOwnedLand.isOwner(player.getUniqueId())) {
            lm.sendMessage(player, lm.getString(player, "Commands.Advertise.notOwn").replace("%owner%",
                    iOwnedLand.getOwnersString()));
            return;
        }

        iOwnedLand.setPrice(price);

        lm.sendMessage(player, lm.getString(player, "Commands.Advertise.success")
                .replace("%landname%", iOwnedLand.getName())
                .replace("%price%", price + ""));
    }
}
