package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/7/17
 */
public class LandMap extends LandlordCommand {


    public void onToggleLandMap(Player player) {

        if (this.worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }

        if (Options.enabled_map())
            plugin.getMapManager().toggleMap(player);
        else {
            lm.sendMessage(player, lm.getString("Commands.LandMap.disabled"));
        }

    }
}
