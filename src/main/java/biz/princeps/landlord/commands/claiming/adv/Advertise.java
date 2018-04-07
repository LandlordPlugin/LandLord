package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offers;
import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Bukkit;
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
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = null;
        if (landname.equals("this")) {
            chunk = player.getWorld().getChunkAt(player.getLocation());
        } else {
            String[] split = landname.split("_");
            try {
                int x = Integer.valueOf(split[1]);
                int z = Integer.valueOf(split[2]);
                chunk = Bukkit.getWorld(split[0]).getChunkAt(x, z);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        OwnedLand pr = plugin.getWgHandler().getRegion(chunk);

        if (pr == null) {
            player.sendMessage(lm.getString("Commands.Advertise.notOwnFreeLand"));
            return;
        }

        if (!pr.isOwner(player.getUniqueId())) {
            player.sendMessage(lm.getString("Commands.Advertise.notOwn").replace("%owner%", pr.printOwners()));
            return;
        }

        Offers offer = new Offers(pr.getName(), price, player.getUniqueId());
        plugin.getPlayerManager().addOffer(offer);

        player.sendMessage(lm.getString("Commands.Advertise.success")
                .replace("%landname%", pr.getName())
                .replace("%price%", price + ""));

    }

}
