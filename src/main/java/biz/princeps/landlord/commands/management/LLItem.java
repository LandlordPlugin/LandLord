package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.items.Maitem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Author: Alex D. (SpatiumPrinceps)
 * <p>
 * Date: 11/7/17 10:45 AM
 */
public class LLItem extends LandlordCommand {

    public void onItem(CommandSender player, String target) {
        Player targetingPlayer = null;

        if (target == null) {
            if (player instanceof Player) {
                targetingPlayer = (Player) player;
            }
        } else
            targetingPlayer = Bukkit.getPlayer(target);

        if (targetingPlayer == null) {
            player.sendMessage(lm.getString("Commands.Item.noPlayer").replace("%player%", target));
            return;
        }

        // now we got our player for sure, lets give him the item
        Maitem item = new Maitem();
        item.give(targetingPlayer);
    }
}
