package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOffer;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offer;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class RemoveAdvertise extends LandlordCommand {

    public RemoveAdvertise(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.RemoveAdvertise.name"),
                pl.getConfig().getString("CommandSettings.RemoveAdvertise.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.RemoveAdvertise.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.RemoveAdvertise.aliases")));
    }


    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        if (Options.isVaultEnabled()) {
            return;
        }

        String landname = arguments.size() == 1 ? arguments.get()[0] : "this";
        Player player = properties.getPlayer();

        if (isDisabledWorld(player, plugin.getWGProxy().getWorld(landname))) return;

        IOwnedLand ownedLand;
        if (landname.equals("this")) {
            ownedLand = plugin.getWGProxy().getRegion(player.getLocation().getChunk());
        } else {
            ownedLand = plugin.getWGProxy().getRegion(landname);
        }

        if (ownedLand == null) {
            lm.sendMessage(player, lm.getString("Commands.Advertise.notOwnFreeLand"));
            return;
        }

        if (!ownedLand.isOwner(player.getUniqueId())) {
            lm.sendMessage(player, lm.getString("Commands.Advertise.notOwn")
                    .replace("%owner%", ownedLand.getOwnersString()));
            return;
        }

        IOffer offer = plugin.getOfferManager().getOffer(ownedLand.getName());
        if (offer == null) {
            lm.sendMessage(player, lm.getString("Commands.RemoveAdvertise.noAdvertise")
                    .replace("%landname%", ownedLand.getName()));
        } else {
            plugin.getOfferManager().removeOffer(offer.getLandname());
            lm.sendMessage(player, lm.getString("Commands.RemoveAdvertise.success")
                    .replace("%landname%", landname));
        }
    }
}
