package biz.princeps.landlord.commands;

import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 29.07.17.
 */
public class SetHome extends LandlordCommand {

    // requires permission landlord.player.home
    public void onSetHome(Player player) {

        if (!plugin.getConfig().getBoolean("Homes.enable")) {
            player.sendMessage(lm.getString("Commands.SetHome.disabled"));
            return;
        }

        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        OwnedLand land = plugin.getWgHandler().getRegion(chunk);

        if (land == null) {
            player.sendMessage(lm.getString("Commands.SetHome.nullLand"));
            return;
        }

        if (!land.isOwner(player.getUniqueId())) {
            player.sendMessage(lm.getString("Commands.SetHome.notOwn")
                    .replace("%owner%", land.printOwners()));
            return;
        }

        plugin.getPlayerManager().get(player.getUniqueId()).setHome(player.getLocation());
        player.sendMessage(lm.getString("Commands.SetHome.success"));
    }
}
