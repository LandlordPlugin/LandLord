package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.commands.LandlordCommand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 */
public class UnclaimAll extends LandlordCommand {

    public void onUnclaim(Player player) {

        if (this.worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }

        List<ProtectedRegion> landsOfPlayer = new ArrayList<>();
        for (World w : Bukkit.getWorlds()) {
            landsOfPlayer.addAll(plugin.getWgHandler().getRegions(player.getUniqueId(), w));
        }

        if (landsOfPlayer.isEmpty()) {
            lm.sendMessage(player, lm.getString("Commands.Unclaim.notOwnFreeLand"));
            return;
        }

        // Normal unclaim
        for (ProtectedRegion protectedRegion : landsOfPlayer) {
            Bukkit.dispatchCommand(player, "ll unclaim " + protectedRegion.getId());
        }

    }

}
