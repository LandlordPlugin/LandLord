package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offer;
import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class RemoveAdvertise extends LandlordCommand {

    public void onRemoveAdvertise(Player player, String landname) {

        if (this.worldDisabled(player)) {
            lm.sendMessage(player,lm.getString("Disabled-World"));
            return;
        }
        OwnedLand pr;
        if (landname.equals("this")) {
            pr = plugin.getWgHandler().getRegion(player.getLocation().getChunk());
        } else {
            pr =plugin.getWgHandler().getRegion(landname);
        }

        if (pr == null) {
            lm.sendMessage(player,lm.getString("Commands.Advertise.notOwnFreeLand"));
            return;
        }

        if (!pr.isOwner(player.getUniqueId())) {
            lm.sendMessage(player,lm.getString("Commands.Advertise.notOwn").replace("%owner%", pr.printOwners()));
            return;
        }

        Offer offer = plugin.getOfferManager().getOffer(pr.getName());
        if (offer == null) {
            lm.sendMessage(player,lm.getString("Commands.RemoveAdvertise.noAdvertise")
                    .replace("%landname%", pr.getName()));
        } else {
            plugin.getOfferManager().removeOffer(offer.getLandname());
            lm.sendMessage(player,lm.getString("Commands.RemoveAdvertise.success")
                    .replace("%landname%", landname));
        }
    }
}
