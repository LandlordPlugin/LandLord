package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offer;
import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Advertise extends LandlordCommand {

    public void onAdvertise(Player player, String landname, double price) {

        if (this.worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }
        OwnedLand pr;
        if (landname.equals("this")) {
            Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
            pr = plugin.getWgHandler().getRegion(chunk);
        } else {
            pr = plugin.getWgHandler().getRegion(landname);
        }

        if (pr == null) {
            lm.sendMessage(player, lm.getString("Commands.Advertise.notOwnFreeLand"));
            return;
        }

        if (!pr.isOwner(player.getUniqueId())) {
            lm.sendMessage(player, lm.getString("Commands.Advertise.notOwn").replace("%owner%", pr.printOwners()));
            return;
        }

        Offer offer = new Offer(pr.getName(), price, player.getUniqueId());
        plugin.getOfferManager().addOffer(offer);

        lm.sendMessage(player, lm.getString("Commands.Advertise.success")
                .replace("%landname%", pr.getName())
                .replace("%price%", price + ""));

    }

}
