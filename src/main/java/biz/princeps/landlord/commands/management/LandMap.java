package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 19.07.17.
 */
public class LandMap extends LandlordCommand {


    public void onToggleLandMap(Player player) {

        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }

        if (Options.enabled_map())
            plugin.getMapManager().toggleMap(player);
        else {
            player.sendMessage(lm.getString("Commands.LandMap.disabled"));
        }

    }
}
