package biz.princeps.landlord.commands.homes;

import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 29/07/17
 */
public class SetHome extends LandlordCommand {

    // requires permission landlord.player.home
    public void onSetHome(Player player) {

        if (!Options.enabled_homes()) {
            lm.sendMessage(player, lm.getString("Commands.SetHome.disabled"));
            return;
        }

        if (this.worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        OwnedLand land = plugin.getWgHandler().getRegion(chunk);

        if (land == null) {
            lm.sendMessage(player, lm.getString("Commands.SetHome.nullLand"));
            return;
        }

        if (!land.isOwner(player.getUniqueId())) {
            lm.sendMessage(player, lm.getString("Commands.SetHome.notOwn")
                    .replace("%owner%", land.printOwners()));
            return;
        }

        plugin.getPlayerManager().get(player.getUniqueId()).setHome(player.getLocation());
        lm.sendMessage(player, lm.getString("Commands.SetHome.success"));
    }
}
