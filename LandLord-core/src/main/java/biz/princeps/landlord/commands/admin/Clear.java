package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ClearType;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMultiTaskManager;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ClearGUI;
import biz.princeps.landlord.multi.MultiClearTask;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/07/17
 */
public class Clear extends LandlordCommand {

    private final IWorldGuardManager wg;
    private final IMultiTaskManager multiTaskManager;

    public Clear(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Clear.name"),
                plugin.getConfig().getString("CommandSettings.Clear.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Clear.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Clear.aliases")));
        this.wg = plugin.getWGManager();
        this.multiTaskManager = plugin.getMultiTaskManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (arguments.size() == 1) {
            // Clear a single player
            String name = arguments.get(0);
            if (properties.getCommandSender().hasPermission("landlord.admin.clear.player"))
                clearPlayer(name, properties.getCommandSender());
            return;
        }

        if (properties.isConsole()) {
            return;
        }
        Player player = properties.getPlayer();

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
                lm.sendMessage(player, lm.getString("Commands.Clear.noPlayer")
                        .replace("%players%", name));
            } else {
                // Success
                multiTaskManager.enqueueTask(new MultiClearTask(plugin, player, wg.getRegions(lPlayer.getUuid()), lPlayer.getName(), ClearType.PLAYER));
            }
        });
    }

}
