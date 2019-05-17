package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offer;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Advertise extends LandlordCommand {

    private IWorldGuardManager wg;

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

        if (isDisabledWorld(player, wg.getWorld(landname))) return;

        IOwnedLand iOwnedLand;
        if (landname.equals("this")) {
            Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
            iOwnedLand = wg.getRegion(chunk);
        } else {
            iOwnedLand = wg.getRegion(landname);
        }

        if (iOwnedLand == null) {
            lm.sendMessage(player, lm.getString("Commands.Advertise.notOwnFreeLand"));
            return;
        }

        if (!iOwnedLand.isOwner(player.getUniqueId())) {
            lm.sendMessage(player, lm.getString("Commands.Advertise.notOwn").replace("%owner%",
                    iOwnedLand.getOwnersString()));
            return;
        }

        Offer offer = new Offer(iOwnedLand.getName(), price, player.getUniqueId());
        plugin.getOfferManager().addOffer(offer);

        lm.sendMessage(player, lm.getString("Commands.Advertise.success")
                .replace("%landname%", iOwnedLand.getName())
                .replace("%price%", price + ""));
    }
}
