package biz.princeps.landlord.commands.friends;


import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 18/07/18
 */
public class ListFriends extends LandlordCommand {

    public void onListFriends(Player player, String landname) {

        if (worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }

        if (landname == null) {
            lm.sendMessage(player, lm.getString("Commands.Listfriends.freeLand"));
            return;
        }

        if (plugin.getWgproxy().isLLRegion(landname)) {
            lm.sendMessage(player, lm.getString("Commands.Listfriends.invalidGeneral"));
            return;
        }

        try {
            IOwnedLand land = plugin.getWgproxy().getRegion(landname);

            if (land == null) {
                lm.sendMessage(player, lm.getString("Commands.Listfriends.freeLand"));
                return;
            }
            if (land.getFriends().size() > 0)
                lm.sendMessage(player, lm.getString("Commands.Listfriends.friends")
                        .replace("%friends%", land.getMembersString()));
            else {
                lm.sendMessage(player, lm.getString("Commands.Listfriends.noFriends"));
            }
        } catch (NumberFormatException ex) {
            lm.sendMessage(player, lm.getString("Commands.Listfriends.invalidGeneral"));
        }
    }
}
