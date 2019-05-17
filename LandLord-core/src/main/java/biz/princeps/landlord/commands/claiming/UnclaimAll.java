package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 * <p>
 * TODO make this class better without 100000 million messages
 */
public class UnclaimAll extends LandlordCommand {

    public UnclaimAll(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.UnclaimAll.name"),
                pl.getConfig().getString("CommandSettings.UnclaimAll.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.UnclaimAll.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.UnclaimAll.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {

        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();

        Set<IOwnedLand> landsOfPlayer = new HashSet<>();
        for (World w : Bukkit.getWorlds()) {
            landsOfPlayer.addAll(plugin.getWGManager().getRegions(player.getUniqueId(), w));
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
