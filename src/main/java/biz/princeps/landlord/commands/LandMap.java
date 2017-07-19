package biz.princeps.landlord.commands;

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

        if (plugin.getConfig().getBoolean("Map.enable"))
            plugin.getMapManager().toggleMap(player);
        else{
            player.sendMessage(lm.getString("Commands.LandMap.disabled"));
        }

    }
}
