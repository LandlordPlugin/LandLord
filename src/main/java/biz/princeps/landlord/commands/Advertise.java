package biz.princeps.landlord.commands;

import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

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




    }

}
