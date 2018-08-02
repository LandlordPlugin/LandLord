package biz.princeps.landlord.commands.friends;


import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 18/07/18
 */
public class ListFriends extends LandlordCommand {

    public void onListFriends(Player player, String landname) {

        if (worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }

        if (landname == null) {
            player.sendMessage(lm.getString("Commands.Listfriends.freeLand"));
            return;
        }
        String[] split = landname.split("_");

        if (split.length != 3) {
            player.sendMessage(lm.getString("Commands.Listfriends.invalidGeneral"));
            return;
        }
        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            player.sendMessage(lm.getString("Commands.Listfriends.invalidWorld"));
            return;
        }
        try {
            Chunk loc = world.getChunkAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            OwnedLand land = plugin.getWgHandler().getRegion(loc);

            if (land == null) {
                player.sendMessage(lm.getString("Commands.Listfriends.freeLand"));
                return;
            }
            if (land.getWGLand().getMembers().size() > 0)
                player.sendMessage(lm.getString("Commands.Listfriends.friends").replace("%friends%", land.printMembers()));
            else
                player.sendMessage(lm.getString("Commands.Listfriends.noFriends"));

        } catch (NumberFormatException ex) {
            player.sendMessage(lm.getString("Commands.Listfriends.invalidGeneral"));
        }
    }
}
