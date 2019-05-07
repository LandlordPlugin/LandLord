package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offer;
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
        IOwnedLand iOwnedLand;
        if (landname.equals("this")) {
            Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
            iOwnedLand = plugin.getWgproxy().getRegion(chunk);
        } else {
            iOwnedLand = plugin.getWgproxy().getRegion(landname);
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
