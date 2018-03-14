package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.CommandUtil;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 17.07.17.
 */
public class Info extends LandlordCommand {

    public void onInfo(Player player) {
        CommandUtil.onInfo(player.getLocation(), player, lm);
    }
}
