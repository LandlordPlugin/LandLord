package biz.princeps.landlord.commands;

import org.bukkit.entity.Player;

/**
 * Created by spatium on 16.07.17.
 */
public class Claim extends LandlordCommand {

    public void onClaim(Player player) {
        player.sendMessage("jo");
        plugin.getWgHandler().claim(player.getWorld().getChunkAt(player.getLocation()));
    }
}
