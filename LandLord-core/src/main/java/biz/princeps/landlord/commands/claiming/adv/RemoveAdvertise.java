package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offer;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class RemoveAdvertise extends LandlordCommand {

    public void onRemoveAdvertise(Player player, String landname) {

        if (this.worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }
        IOwnedLand ownedLand;
        if (landname.equals("this")) {
            ownedLand = plugin.getWgproxy().getRegion(player.getLocation().getChunk());
        } else {
            ownedLand = plugin.getWgproxy().getRegion(landname);
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

        Offer offer = plugin.getOfferManager().getOffer(ownedLand.getName());
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
