package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Project: LandLord
 * Author: Alex D. (SpatiumPrinceps)
 * <p>
 * Date: 11/7/17 10:45 AM
 */
public class LLItem extends LandlordCommand{

    public void onItem(Player player, String target){
        Player targetingPlayer;

        if(target.equals("null"))
            targetingPlayer = player;
        else
            targetingPlayer = Bukkit.getPlayer(target);

        if(targetingPlayer == null){
            player.sendMessage(lm.getString("Commands.Item.noPlayer").replace("%player%", target));
            return;
        }

        // now we got our player for sure, lets give him the item


    }

}
