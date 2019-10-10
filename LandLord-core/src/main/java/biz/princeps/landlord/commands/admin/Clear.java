package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ClearGUI;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/07/17
 */
public class Clear extends LandlordCommand {

    private IWorldGuardManager wg;

    public Clear(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Clear.name"),
                pl.getConfig().getString("CommandSettings.Clear.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Clear.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Clear.aliases")));
        this.wg = pl.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {

        if (arguments.size() == 1){
            // Clear a single player
            String name = arguments.get()[0];
            clearPlayer(name, properties.getCommandSender());
            return;
        }

        if (properties.isConsole()) {
            return;
        }
        Player player = properties.getPlayer();

        if (isDisabledWorld(player)) return;

        /*
         * Clear Options:
         * 1. Clear all for player x        (target==x || player stands inside x claim)
         * 2. Clear only specific claim     (target==null)
         * 3. Clear entire world            (target==null)
         */
        ClearGUI clearGUI = new ClearGUI(plugin, player);
        clearGUI.display();
    }

    private void clearPlayer(String name, CommandSender player) {
        plugin.getPlayerManager().getOffline(name, (lPlayer) -> {
            if (lPlayer == null) {
                // Failure
                lm.sendMessage(player, lm.getString("Commands.ClearWorld.noPlayer")
                        .replace("%players%", name));
            } else {
                // Success
                int amt = wg.unclaim(wg.getRegions(lPlayer.getUuid()));

                lm.sendMessage(player, lm.getString("Commands.ClearWorld.gui.clearplayer.success")
                        .replace("%count%", String.valueOf(amt))
                        .replace("%player%", lPlayer.getName()));

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getPlugin(),
                        () -> plugin.getMapManager().updateAll());
            }
        });
    }
}
