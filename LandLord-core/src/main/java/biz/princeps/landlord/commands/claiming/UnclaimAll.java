package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 *
 * TODO make this class better without 100000 million messages
 */
public class UnclaimAll extends LandlordCommand {

    public UnclaimAll(ILandLord plugin) {
        super(plugin);
    }

    public void onUnclaim(Player player) {

        Set<IOwnedLand> landsOfPlayer = new HashSet<>();
        for (World w : Bukkit.getWorlds()) {
            landsOfPlayer.addAll(plugin.getWGProxy().getRegions(player.getUniqueId(), w));
        }

        if (landsOfPlayer.isEmpty()) {
            lm.sendMessage(player, lm.getString("Commands.Unclaim.notOwnFreeLand"));
            return;
        }

        // Normal unclaim
        for (IOwnedLand ol : landsOfPlayer) {
            Bukkit.dispatchCommand(player, "ll unclaim " + ol.getName());
        }

    }

}
